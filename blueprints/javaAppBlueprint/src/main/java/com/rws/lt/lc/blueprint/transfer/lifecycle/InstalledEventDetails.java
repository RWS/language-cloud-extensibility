package com.rws.lt.lc.blueprint.transfer.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstalledEventDetails {

    @Valid
    @NotNull
    @NotBlank
    public String region;
}
