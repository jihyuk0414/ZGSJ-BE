package com.example.core_bank.core_bank.core.service;

import com.example.core_bank.core_bank.core.dto.transfer.TransferRequest;
import com.example.core_bank.core_bank.core.model.Account;
import com.example.core_bank.core_bank.core.model.Classfication;
import com.example.core_bank.core_bank.core.model.TransactionHistory;
import com.example.core_bank.core_bank.core.repository.AccountRepository;
import com.example.core_bank.core_bank.core.repository.TransactionHistoryRepository;
import com.example.core_bank.core_bank.global.error.CustomException;
import com.example.core_bank.core_bank.global.error.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankCoreService {

    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final EntityManager entityManager;

    @Transactional
    public LocalDate transfer(TransferRequest transferRequest) {

        // 리스트 정렬해서 받음.
        // 여기 로직 더 효율적이게 변경 되어야함
        try{

            log.info("{} {} {} ",transferRequest.getToAccount(),transferRequest.getToBankCode(),transferRequest.getToAccountDepositor());
            log.info("{} {} {} ",transferRequest.getFromAccount(),transferRequest.getFromBankCode(),transferRequest.getFromAccountDepositor());
            log.info("첫째");
            Account fromAccount = findFromAccount(transferRequest);
            log.info("둘째");
            Account toAccount = findToAccount(transferRequest);

            validateBalance(fromAccount, transferRequest.getAmount());

            LocalDate now = LocalDate.now();
            updateBalances(fromAccount, toAccount, transferRequest.getAmount());
            createHistories(fromAccount, toAccount,transferRequest.getAmount(),now);
            return now;
        } catch (Exception e)
        {
            log.info(e.getMessage()+"오류발생");
            return null;
        }
    }

    private void updateBalances(Account fromAccount, Account toAccount, Long amount){
        Long fromResult=fromAccount.getBalance() -amount;
        Long toResult=toAccount.getBalance() + amount;
        accountRepository.updateBalances(fromAccount.getId(),toAccount.getId(),fromResult,toResult);
    }

    private void createHistories(Account fromAccount, Account toAccount,Long amount,LocalDate now) {
        Classfication transfer = entityManager.getReference(Classfication.class, 3);
        Classfication deposit = entityManager.getReference(Classfication.class, 26);
        List<TransactionHistory> transactionHistories =Arrays.asList(TransactionHistory.createTransactionHistory
                        (now, amount,false, "이체", toAccount.getName(),fromAccount, transfer),
                TransactionHistory.createTransactionHistory
                        (now,amount, true, "입금", fromAccount.getName(),toAccount, deposit));
        transactionHistoryRepository.saveAll(transactionHistories);
    }

    private void validateBalance(Account account, Long amount) {
        if (account.getBalance() < amount) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

    private Account findAccount(String accountNumber, String bankCode, String depositorName) {
        return accountRepository.findByAccountNumberAndBankCodeAndName(
                        accountNumber,
                        bankCode,
                        depositorName)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Account findFromAccount(TransferRequest request){
        return findAccount(
                request.getFromAccount(),
                request.getFromBankCode(),
                request.getFromAccountDepositor()
        );
    }

    private Account findToAccount(TransferRequest request) {
        return findAccount(
                request.getToAccount(),
                request.getToBankCode(),
                request.getToAccountDepositor()
        );
    }
}
