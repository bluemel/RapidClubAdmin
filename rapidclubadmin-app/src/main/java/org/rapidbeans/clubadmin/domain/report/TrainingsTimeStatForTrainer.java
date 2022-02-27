package org.rapidbeans.clubadmin.domain.report;

import java.math.BigDecimal;

import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;

public class TrainingsTimeStatForTrainer {

	private final TrainerDepRole trainerDepRole;
	
	private Time time = new Time(BigDecimal.ZERO, UnitTime.min);
	private Money money = new Money(BigDecimal.ZERO, Currency.euro);

	public TrainingsTimeStatForTrainer(TrainerDepRole trainerDepRole) {
		this.trainerDepRole = trainerDepRole;
	}

	public void addTime(Time additionalTime) {
		this.time = (Time) this.time.add(additionalTime);
	}

	public void addMoney(Money additionalMoney) {
		this.money = (Money) this.money.add(additionalMoney);
	}

	public TrainerDepRole getTrainerDepRole() {
		return trainerDepRole;
	}

	public Time getTime() {
		return time;
	}

	public Money getMoney() {
		return money;
	}	
}
