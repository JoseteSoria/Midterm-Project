
insert into user(id, name, username) values
(1, 'Peter Pan', 'petpan'),
(2, 'Wendy Darling', 'wendy'),
(3, 'James Garfio', 'capgarfio');

insert into account_holder(id, date_of_birthday, mailing_city, mailing_country, mailing_street, primary_city,
primary_country, primary_street) values
(1, '1970-04-21', 'Madrid', 'Spain', 'Gran Via', 'Madrid', 'Spain', 'Canal'),
(2, '1985-06-13', null, null, null, 'Murcia', 'Spain', 'Traperia'),
(3, '1960-12-10', 'Pirate Ship', 'Neverland', 'Main Cabin', 'Ocean', 'Neverland', 'Skull Island');

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
