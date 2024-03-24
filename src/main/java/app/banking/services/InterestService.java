package app.banking.services;

import app.banking.DTO.UpSetInterestPayload;
import app.banking.models.Account;
import app.banking.models.Interest;
import app.banking.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestService {
  private final InterestRepository interestRepo;

  public Optional<Interest> getByAccountId(Long id){
    return interestRepo.findByAccountId(id);
  }

  public Optional<Interest> createOrUpdate(UpSetInterestPayload payload){
    Interest interest = new Interest();
    Long accountId = payload.getIdAccount();
    interest.setIdAccount(Account.onlyId(accountId));
    interest.setEffectiveInterest(payload.getEffectiveInterest());
    interest.setFirstInterest(payload.getFirstInterest());
    interest.setMaxDaysPayment(7L);
    Optional<Interest> accountInterest = interestRepo.findByAccountId(accountId);
    if (accountInterest.isPresent()) {
      return interestRepo.updateById(
        accountInterest.get().getId(),
        interest
      );
    }
    return interestRepo.save(interest);
  }
}
