package Models;

import java.time.LocalDateTime;

import DataAccess.BankAccountDA;

public class Transaction {
	private int tranID;
	private int fromId;
	private int receiveId;
	private LocalDateTime tranTime;
	private String tranType;
	private double tranAmount;
	private boolean transferred;

	public Transaction(int tranID, int fromId, int receiveId, LocalDateTime tranTime, String tranType,
			double tranAmount, boolean transferred) {
		super();
		this.tranID = tranID;
		this.fromId = fromId;
		this.receiveId = receiveId;
		this.tranTime = tranTime;
		this.tranType = tranType;
		this.tranAmount = tranAmount;
		this.transferred = transferred;
	}

	// for create
	public Transaction(int fromId, int receiveId, String tranType, double tranAmount) {
		super();

		try {
			this.fromId = fromId;
			this.receiveId = receiveId;
			this.tranTime = LocalDateTime.now();
			this.tranType = tranType;
			this.tranAmount = tranAmount;

			BankAccountDA accDA = new BankAccountDA();
			BankAccount fromAcc = accDA.GetAccount(fromId);
			BankAccount receiveAcc = accDA.GetAccount(receiveId);
			if (insufficientSenderBalance(fromAcc)) {
				throw new InsufficientBalanceException("Insufficient balance to complete the transaction");
			} else {

				if (tranType.equals("Third Party Transfer")) {

					if (!fromAcc.getBankType().equals(receiveAcc.getBankType())) {
						System.out.println("error");
						return;
					}

					fromAcc.setBalance(fromAcc.getBalance() - tranAmount);
					receiveAcc.setBalance(receiveAcc.getBalance() - tranAmount);

					accDA.updateAccount(fromAcc);
					accDA.updateAccount(receiveAcc);

				} else if (tranType.equals("IBGT")) {

					if (fromAcc.getBankType().equals(receiveAcc.getBankType())) {
						System.out.println("error");
						return;
					}

					fromAcc.setBalance(fromAcc.getBalance() - tranAmount - 0.11);
					receiveAcc.setBalance(receiveAcc.getBalance() - tranAmount);

					accDA.updateAccount(fromAcc);
					accDA.updateAccount(receiveAcc);

				} else if (tranType.equals("IBG")) {

					if (fromAcc.getBankType().equals(receiveAcc.getBankType())) {
						System.out.println("error");
						return;
					}

					fromAcc.setBalance(fromAcc.getBalance() - tranAmount);
					receiveAcc.setBalance(receiveAcc.getBalance() - tranAmount);

					accDA.updateAccount(fromAcc);
					accDA.updateAccount(receiveAcc);

				}
				else {
					System.out.println("error");
				}
			}

		} catch (InsufficientBalanceException e) {
			System.out.println(e.getMessage());
			return;
		}

		changeTranferredByType(tranType);
	}

	private boolean insufficientSenderBalance(BankAccount fromAcc) {
		String tranType = this.tranType;
		double tranAmount = this.tranAmount;

		if (fromAcc == null) {
			return true;
		}

		double balance = fromAcc.getBalance();
		double minusValue = tranAmount;

		if (fromAcc.getAccType().equals("Savings")) {
			if ("IBGT".equals(tranType)) {

				minusValue += 0.11;

			}

			if ((balance - minusValue) >= 20) {

				return false;

			}
		} else if (fromAcc.getAccType().equals("Current")) {
			if ("IBGT".equals(tranType)) {

				minusValue += 0.11;

			}

			if ((balance - minusValue) >= 250) {

				return false;

			}
		} else {
			System.out.println("error");
		}

		return true;

	}

	// 自定义异常类，用于表示余额不足的异常情况
	class InsufficientBalanceException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6229660811026665697L;

		public InsufficientBalanceException(String message) {
			super(message);
		}
	}

	private void changeTranferredByType(String tranType) {
		if ("IBG".equals(tranType)) {
			this.transferred = false;
		} else if ("IBGT".equals(tranType)) {
			this.transferred = true;
		}
	}

	public int getTranID() {
		return tranID;
	}

	public void setTranId(int tranID) {
		this.tranID = tranID;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(int receiveId) {
		this.receiveId = receiveId;
	}

	public LocalDateTime getTranTime() {
		return tranTime;
	}

	public void setTranTime(LocalDateTime tranTime) {
		this.tranTime = tranTime;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public double getTranAmount() {
		return tranAmount;
	}

	public void setTranAmount(double tranAmount) {
		this.tranAmount = tranAmount;
	}

	public boolean isTransferred() {
		return transferred;
	}

	public void setTransferred(boolean transferred) {
		this.transferred = transferred;
	}

}
