package com.zenjob.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class ResponseDtoWrapper<K> {
    K data;
    List<Status> errors = new ArrayList<>();
    List<Status> warnings = new ArrayList<>();


    public  ResponseDtoWrapper setData(K data) {
        this.data = data;
        return this;
    }

    public ResponseDtoWrapper addError(Status error) {
        this.errors.add(error);
        return this;
    }


    public ResponseDtoWrapper addWarning(Status warning) {
        this.warnings.add(warning);
        return this;

    }

    @Builder
    @AllArgsConstructor
    @Data
    public static class Status {
        Integer code;
        String message;

    }
}
