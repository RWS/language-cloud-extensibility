package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RetryableRepositoryTest.TestConfiguration.class)
@SpringBootTest()
@ActiveProfiles("base-mongo-test")
public class RetryableRepositoryTest {
    @Configuration
    @Profile("base-mongo-test")
    static class TestConfiguration {
        @Bean
        @Primary
        public MongoTemplate mongoTemplate() {
            return mock(MongoTemplate.class);
        }


        @Bean
        public MongoEntityInformation<TestModel, String> mongoEntityInformation() {
            return mock(MongoEntityInformation.class);
        }

        @Bean
        @Primary
        public TestRepository testBaseMongoRepository() {
            return new TestRetryableMongoRepositoryImpl(mongoEntityInformation(), mongoTemplate());
        }
    }

    static class TestModel {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    interface TestRepository extends RetryableRepository<TestModel, String> {
    }

    static class TestRetryableMongoRepositoryImpl extends RetryableRepositoryImpl<TestModel, String> implements TestRepository {

        private final MongoOperations mongoOperations;

        public TestRetryableMongoRepositoryImpl(MongoEntityInformation<TestModel, String> metadata, MongoOperations mongoOperations) {
            super(metadata, mongoOperations);
            this.mongoOperations = mongoOperations;
        }

        @Override
        public TestModel save(TestModel testModel) {
            mongoOperations.save(testModel);
            return testModel;
        }

        @Override
        public Optional<TestModel> findById(String id) {
            return Optional.ofNullable(mongoOperations.findById(id, TestModel.class));
        }

    }

    private static final String TEST_ID = "ID";

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Before
    public void setUp() {
        reset(mongoTemplate);
    }

    @Test
    public void testUpdateWhenNoFails() throws NotFoundException {
        TestModel testModel = new TestModel();
        testModel.setValue("before");

        doReturn(testModel).when(mongoTemplate).findById(eq(TEST_ID), eq(TestModel.class));

        testRepository.update(TEST_ID, t -> t.setValue("after"));

        ArgumentCaptor<TestModel> argumentCaptor = ArgumentCaptor.forClass(TestModel.class);
        verify(mongoTemplate, times(1)).findById(eq(TEST_ID), eq(TestModel.class));
        verify(mongoTemplate, times(1)).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getValue(), is("after"));
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateWhenEntityNotFound() throws NotFoundException {
        TestModel testModel = new TestModel();
        testModel.setValue("before");


        doReturn(null).when(mongoTemplate).findById(eq(TEST_ID), eq(TestModel.class));

        testRepository.update(TEST_ID, t -> t.setValue("after"));
        verify(mongoTemplate, never()).save(any(TestModel.class));
    }

    @Test
    public void testUpdateWhenFirstAttemptsFail() throws Exception {
        doReturn(new TestModel()).when(mongoTemplate).findById(eq(TEST_ID), eq(TestModel.class));
        doThrow(new OptimisticLockingFailureException("first fail"))
                .doThrow(new OptimisticLockingFailureException("second fail"))
                .doThrow(new OptimisticLockingFailureException("third fail"))
                .doThrow(new OptimisticLockingFailureException("fourth fail"))
                .doReturn(mock(TestModel.class))
                .when(mongoTemplate).save(any(TestModel.class));

        testRepository.update(TEST_ID, t -> t.setValue("after"));

        verify(mongoTemplate, times(5)).findById(eq(TEST_ID), eq(TestModel.class));
        verify(mongoTemplate, times(5)).save(any(TestModel.class));
    }

    @Test(expected = OptimisticLockingFailureException.class)
    public void testUpdateWhenTooManyAttemptsFail() throws Exception {
        doReturn(new TestModel()).when(mongoTemplate).findById(eq(TEST_ID), eq(TestModel.class));
        doThrow(new OptimisticLockingFailureException("first fail"))
                .doThrow(new OptimisticLockingFailureException("second fail"))
                .doThrow(new OptimisticLockingFailureException("third fail"))
                .doThrow(new OptimisticLockingFailureException("fourth fail"))
                .doThrow(new OptimisticLockingFailureException("fifth fail"))
                .when(mongoTemplate).save(any(TestModel.class));

        testRepository.update(TEST_ID, t -> t.setValue("after"));
    }
}