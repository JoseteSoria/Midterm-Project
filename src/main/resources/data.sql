
insert into user(id, name, username, role) values
(1, 'Peter Pan', 'petpan', 'ACCOUNT_HOLDER'),
(2, 'Wendy Darling', 'wendy', 'ACCOUNT_HOLDER'),
(3, 'James Garfio', 'capgarfio', 'ACCOUNT_HOLDER'),
(4, 'Sir Shrek', 'shogro', 'ACCOUNT_HOLDER'),
(5, 'Walt Disney', 'wdisney', 'ADMIN');

insert into admin values (5);

insert into account_holder(id, date_of_birthday, mailing_city, mailing_country, mailing_street, primary_city,
primary_country, primary_street) values
(1, '1999-04-21', 'Madrid', 'Spain', 'Gran Via', 'Madrid', 'Spain', 'Canal'),
(2, '1985-06-13', null, null, null, 'Murcia', 'Spain', 'Traperia'),
(3, '1960-12-10', 'Pirate Ship', 'Neverland', 'Main Cabin', 'Ocean', 'Neverland', 'Skull Island'),
(4, '1990-02-20', 'Wood', 'Dunlop', 'Cienaga', 'Far Far Away', 'Dreamland', 'Royal Palace');

insert into account (id, balance_amount, balance_currency, penalty_fee_amount, penalty_fee_currency,
primary_owner_id, secondary_owner_id) values
(1, 4000, 'USD', 40, 'USD', 1, 2),
(2, 2500, 'USD', 40, 'USD', 1, null),
(3, 1000, 'USD', 40, 'USD', 4, null),
(4, 9000, 'USD', 40, 'USD', 3, 4);

insert into checking_acc (id, min_balance_amount, min_balance_currency, month_fee_amount,
month_fee_currency, secret_key, status) values
(2, 1000, 'USD', 12, 'USD', 'asjhe263q', 'ACTIVE');

insert into student_checking_acc (id, secret_key, status) values
(1, '2iwqdfsqew12', 'ACTIVE');

ALTER TABLE savings_acc Modify interest_rate decimal(17,4);

insert into savings_acc (id, date_interest_rate, interest_rate,
min_balance_amount, min_balance_currency, secret_key, status) values
(4, '2020-05-22', 0.0025, 200, 'USD', 'mcnvw23', 'ACTIVE');

insert into credit_card_acc(id, credit_limit_amount, credit_limit_currency, date_interest_rate, interest_rate) values
(3, 50000, 'USD', '2020-05-10', 0.2);

--insert into account_holder values
--(1,'Peter Pan', '1970-04-21', 'Madrid', 'Spain', 'Gran Via', 'Madrid', 'Spain', 'Canal'),
--(2,'Wendy Darling', '1985-06-13', null, null, null,'Murcia', 'Spain', 'Traperia'),
--(3,'James Garfio', '1960-12-10', 'Pirate Ship', 'Neverland', 'Main Cabin', 'Ocean', 'Neverland', 'Skull Island');
--
--insert into checking_acc values
--(1, 10000, 'USD', 40, 'USD', 1, null, 250, 'USD', 12, 'USD', '1234', 'ACTIVE');
--
--insert into credit_card_acc (id, balance_amount, balance_currency, penalty_fee_amount, penalty_fee_currency,
--primary_owner_id, secondary_owner_id, credit_limit_amount, credit_limit_currency, interest_rate) values
--(1, 6500, 'USD', 40, 'USD', 2, 1, 1500, 'USD', 0.2);
--
--insert into student_checking_acc values
--(1, 8500, 'USD', 40, 'USD', 1, 2, '5678', 'ACTIVE');
--
--insert into savings_acc values
--(1, 7000, 'USD', 40, 'USD', 1, 2, 0.2, 800, 'USD', 'abcd', 'ACTIVE');
