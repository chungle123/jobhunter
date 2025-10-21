package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPagination {
    private Meta meta;
    private Object result;
}
