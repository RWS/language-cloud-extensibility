﻿using MongoDB.Driver;
using Rws.LC.AppBlueprint.DAL.Entities;
using Rws.LC.AppBlueprint.Interfaces;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.DAL
{
    public class AppRegistrationRepository : IAppRegistrationRepository
    {
        /// <summary>
        /// The registration status collection.
        /// </summary>
        private readonly IMongoCollection<AppRegistrationEntity> _appRegistration;

        /// <summary>
        /// The database context.
        /// </summary>
        private readonly IDatabaseContext _databaseContext;

        /// <summary>
        /// Initializes a new instance of the <see cref="RegistrationRepository"/> class.
        /// </summary>
        /// <param name="databaseContext">The database context.</param>
        public AppRegistrationRepository(IDatabaseContext databaseContext)
        {
            _databaseContext = databaseContext;
            _appRegistration = _databaseContext.Mongo.GetCollection<AppRegistrationEntity>("AppRegistration");
        }

        /// <summary>
        /// Saves the app registration entity.
        /// </summary>
        /// <param name="entity">The app registration entity.</param>
        /// <returns></returns>
        public async Task SaveRegistrationInfo(AppRegistrationEntity entity)
        {
            // there should be only one registered document in the collection
            var emptyFilter = FilterDefinition<AppRegistrationEntity>.Empty;
            var update = Builders<AppRegistrationEntity>.Update.Set(e => e.ClientCredentials, entity.ClientCredentials);
            var updateOptions = new UpdateOptions { IsUpsert = true };

            await _appRegistration.UpdateOneAsync(emptyFilter, update, updateOptions).ConfigureAwait(false);
        }

        /// <summary>
        /// Retrieves the app registration entity.
        /// </summary>
        /// <returns>The app registration entity</returns>
        public async Task<AppRegistrationEntity> GetRegistrationInfo()
        {
            return await _appRegistration.Find(FilterDefinition<AppRegistrationEntity>.Empty).SingleOrDefaultAsync().ConfigureAwait(false);
        }

        /// <summary>
        /// Removes the app registration entity.
        /// </summary>
        /// <returns></returns>
        public async Task RemoveRegistrationInfo()
        {
            await _appRegistration.DeleteOneAsync(FilterDefinition<AppRegistrationEntity>.Empty).ConfigureAwait(false);
        }
    }
}
