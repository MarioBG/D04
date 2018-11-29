package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import domain.CreditCard;
import repositories.CreditCardRepository;

@Service
@Transactional
public class CreditCardService {
	
	//Managed repository
	@Autowired
	private CreditCardRepository creditCardRepository;
	
	//Constructor
	public CreditCardService(){
		super();
	}
	
	//Simple CRUD methods
	
	public CreditCard create(){
		CreditCard res;
		
		res = new CreditCard();
		
		return res;
	}
	
	public CreditCard save(CreditCard creditCard){
		Assert.notNull(creditCard);
		Assert.isTrue(checkExpiration(creditCard), "message.error.expiration");
		
		CreditCard result;

		result = creditCardRepository.save(creditCard);

		return result;
	}

	public void delete(CreditCard creditCard){
		Assert.notNull(creditCard);
		
		creditCardRepository.delete(creditCard);
	}
	
	public Collection<CreditCard> findAll(){
		
		Collection<CreditCard> result;

		result = creditCardRepository.findAll();
		Assert.notNull(result);

		return result;
	}
	
	public CreditCard findOne(int creditCardId){
		CreditCard result;

		result = creditCardRepository.findOne(creditCardId);

		return result;
	}
	
	//Other business methods
	
	public boolean checkExpiration(CreditCard c){
		Boolean res = true;
		
	    if((c.getExpirationYear()==LocalDate.now().getYear() 
	    		&& (c.getExpirationMonth() == LocalDate.now().getMonthOfYear()
	    				|| c.getExpirationMonth() < LocalDate.now().getMonthOfYear()))
	    	|| c.getExpirationYear()<LocalDate.now().getYear()){
	    	res = false;
	    }
	    
	    return res;
	}

}
