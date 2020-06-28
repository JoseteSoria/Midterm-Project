# Midterm Project

## Banking System  
---

This project seeks to provide a solution to manage some types of account which could be in a "any" banking system. 
The project covers how to deal with those kinds of account with the system an with the different user types.

Feel free to check the [Documentation](https://documenter.getpostman.com/view/7732232/T17AkWYH?version=latest) to understand the query possibilities and to find some examples of them.
You have to know that all routes are protected and you will need authoritation.

Although in the _data.sql_ file you could find and example to initialize the database, the only things are needed: 
* A mysql database called _banking_system_. 
* Another database called _banking_system_test_ in order to test the program.
* At least one admin to be able to begin to create the rest of the accounts/users/operations.
You could create one inserting in the database, when the program is running, something like this:
````sh
insert into user(id, name, username, password, role) values
(1, 'Admin', 'admin', '$2a$10$YibhRjaKLMpjtofvAOFpveefH0qupJt4/LXGEAY.yzmkWnt8ciHcq', 'ADMIN'); 

insert into admin values (1);
````
*In this case password is *admin*


Do not forget to properly configure the application.properties!

The only things to add up are: 
* For the fraud section the admin operations have not been taking into account.
* The admin can change the Status of the account when manually.
* Due to the credit card do not have Status or secretKey _Third Party_ users can not interact with them and that kind of account can not be frozen (the fraud conditions are anyways considered but they will not freeze the account).

You will find below the official project requirements. 

---
#### Official requirements
**1-** The system must have 4 types of accounts: StudentChecking, Checking, Savings, and CreditCard

**Checking**
Checking Accounts should have
* a balance
* a secretKey
* a PrimaryOwner
* an optional SecondaryOwner
* a minimumBalance
* a penaltyFee
* a monthlyMaintenanceFee
* a status (FROZEN, ACTIVE)

**StudentChecking**
Student Checking Accounts are identical to Checking Accounts except that they do NOT have

* a monthlyMaintenanceFee
* a minimumBalance

**Savings**
Savings are identical to Checking accounts except that they
* do NOT have a monthlyMaintenanceFee
* do have an interestRate

**CreditCard**
CreditCard Accounts have
* a balance
* a PrimaryOwner
* an optional SecondaryOwner
* a creditLimit
* an interestRate
* a penaltyFee   

**2-** The system must have 3 types of Users: Admins and AccountHolders.

**AccountHolders**
AccountHolders should be able to login, logout, and access their own account. AccountHolders have:
* a name
* a date of birth
* a primaryAddress (which should be a separate address class)
* an optional mailingAddress

**Admins**
Admins only have a name

**ThirdParty**
Third Party Accounts have a hashed key and a name.

**3-** Admins can create new accounts. When creating a new account they can create Checking, Savings, or CreditCard Accounts.

**Savings**
* savings accounts have a default interest rate of 0.0025
* savings accounts may be instantiated with an interest rate other than the default, with a maximum interest rate of 0.5
* savings accounts should have a default minimumBalance of 1000
* savings accounts may be instantiated with a mimimum balance of less than 1000 but no lower than 100

**CreditCards**
* creditCard accounts have a default creditLimit of 100
* creditCards may be instantiated with a creditLimit higher than 100 but not higher than 100000
* creditCards have a default interestRate of 0.2
* creditCards may be instantiated with an interestRate less than 0.2 but not lower than 0.1

**CheckingAcounts**
* When creating a new Checking account, if the primaryOwner is less than 24, a StudentChecking account should be created otherwise a regular Checking Account should be created.
* checking accounts should have a minimumBalance of 250 and a monthlyMaintenanceFee of 12


**4-** Interest and Fees should be applied appropriately

**PenaltyFee**
* The penaltyFee for all accounts should be 40.
If any account drops below the minimumBalance, the penaltyFee should be deducted from the balance automatically.

**Interest Rates**
* Interest on savings accounts is added to the account annually at the rate of specified interestRate per year. That means that if I have 1000000 in a savings account with a 0.01 interest rate, 1% of 1 Million is added to my account after 1 year. When a savings Account balance is accessed, you must determine if it has been 1 year or more since the either the account was created or since interest was added to the account, and add the appropriate interest to the balance if necessary.

* Interest on credit cards is added to the balance monthly. If you have a 12% interest rate (0.12) then 1% interest will be added to the account monthly. When the balance of a credit card is accessed, check to determine if it has been 1 month or more since the account was created or since interested was added, and if so, add the appropriate interest to the balance.

**5-** Account Access

**Admins**
* Admins should be able to access the balance for any account, to debit the balance, and to credit the balance.

**AccountHolders**
* AccountHolders should be able to access their own account balance
* Account holders should be able to transfer money from any of their accounts to any other account (regardless of owner). The transfer should only be processed if the account has sufficient funds. The user must provide the Primary or Secondary owner name and the id of the account that should receive the transfer.

**Third Party Users**
* There must be a way for third party users to debit and credit accounts.
Third party accounts must be added to the database by an admin.
Third Party users can debit or credit accounts of any type. To do so the must provide their hashed key in the header of the HTTP request. They also must provide the amount, the Account id and the account secret key.

**6-** Fraud Detection

The application must recognize patterns that indicate fraud and Freeze the account status when potential fraud is detected.

Patterns that indicate fraud include:

* Transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.
* More than 2 transactions occuring on a single account within a 1 second period.

**7-** Logging

All account access and transactions must be logged in a mongo database with user ids for auditing purposes.