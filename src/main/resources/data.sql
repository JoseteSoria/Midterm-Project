
insert into user(id, name, username, password, role) values
(1, 'Peter Pan', 'petpan', '$2a$10$k1D7W/7EpNa6HcjbTa4IreVCwsZc1rs5vzK3299R4LvccU9YAMQ.u', 'ACCOUNT_HOLDER'),
(2, 'Wendy Darling', 'wendy', '$2a$10$nRYxziFL.ZAvWzzlzeXklOkJ/NJncMTqT8098T9GfUXWvfILyYZK.', 'ACCOUNT_HOLDER'),
(3, 'James Garfio', 'capgarfio', 'rahwo','ACCOUNT_HOLDER'),
(4, 'Sir Shrek', 'shogro', 'sdjaghe', 'ACCOUNT_HOLDER'),
(5, 'Walt Disney', 'wdisney', '34ufdhi34h98', 'ADMIN'),
(6, 'Admin', 'admin', '$2a$10$YibhRjaKLMpjtofvAOFpveefH0qupJt4/LXGEAY.yzmkWnt8ciHcq', 'ADMIN'),
(7, 'Tercero', 'tercero', '$2a$10$NQXS7948wqOvTLeHF3jTGeXhGiga5p9L20atvo3PnNo6YQ1J/7gzK', 'THIRD_PARTY');


insert into admin values (5);

insert into third_party (id, hash_key) values
(7, '$2a$10$h8OoLWY.3v4ZLLYGwouQh.rzdOGqGekTX1nMTpjQdMHj8Q6SebCu.');

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
(2, 1000, 'USD', 12, 'USD', 'ES26342347232349906672', 'ACTIVE');

insert into student_checking_acc (id, secret_key, status) values
(1, 'ES26142554295712900012', 'ACTIVE');

ALTER TABLE savings_acc Modify interest_rate decimal(17,4);

insert into savings_acc (id, date_interest_rate, interest_rate,
min_balance_amount, min_balance_currency, secret_key, status) values
(4, '2020-05-22', 0.0025, 200, 'USD', 'ES2490263816471883671', 'ACTIVE');

insert into credit_card_acc(id, credit_limit_amount, credit_limit_currency, date_interest_rate, interest_rate) values
(3, 50000, 'USD', '2020-05-10', 0.2);

insert into transaction (id, ordering_id, sender_account_id, beneficiary_account_id, amount, currency, date, transaction_type) values
(1, 1, 1, 2, 1000, 'USD', '2020-06-23', 'TRANSFERENCE'),
(2, 2, 2, null, 2000, 'USD', '2020-06-22', 'CREDIT'),
(3, 1, 1, 2, 15000, 'USD', '2020-06-18', 'TRANSFERENCE'),
(4, 5, null, 1, 3100, 'USD', '2020-06-19', 'DEBIT'),
(5, 2, 2, 3, 30000, 'USD', '2020-06-23', 'TRANSFERENCE'),
(6, 3, null, 3, 2300, 'USD', '2020-06-22', 'DEBIT'),
(7, 1, 1, 3, 2300, 'USD', '2020-06-02', 'TRANSFERENCE'),
(8, 2, 2, 3, 200, 'USD', '2020-06-20', 'TRANSFERENCE'),
(9, 2, 2, 3, 6000, 'USD', '2020-06-18', 'TRANSFERENCE');
