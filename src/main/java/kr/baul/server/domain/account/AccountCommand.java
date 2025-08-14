package kr.baul.server.domain.account;

import lombok.Builder;

public record AccountCommand(
) {

    @Builder
    public record AccountCharge(
            Long userId,
            Long amount
    ){

    }

}
