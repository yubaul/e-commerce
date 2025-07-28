package kr.baul.server.interfaces.account;

import kr.baul.server.application.account.AccountCommand;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AccountDtoMapper {

    AccountCommand.AccountCharge of(AccountDto.AccountChargeRequest request);
}
