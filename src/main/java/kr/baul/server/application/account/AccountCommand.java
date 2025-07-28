package kr.baul.server.application.account;

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
