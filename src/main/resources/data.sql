insert into currencies (code, fullname, sign) values ('USD', 'US Dollar', '$');
insert into currencies (code, fullname, sign) values ('EUR', 'Euro', '€');
insert into currencies (code, fullname, sign) values ('RUB', 'Russian Ruble', '₽');
insert into currencies (code, fullname, sign) values ('UAH', 'Hryvnia', '₴');
insert into currencies (code, fullname, sign) values ('KZT', 'Tenge', '₸');
insert into currencies (code, fullname, sign) values ('GBP', 'Pound Sterling', '£');

insert into exchange_rates (base_currency_id, target_currency_id, rate) values (1, 2, 0.94);
insert into exchange_rates (base_currency_id, target_currency_id, rate) values (1, 3, 63.75);
insert into exchange_rates (base_currency_id, target_currency_id, rate) values (1, 4, 36.95);
insert into exchange_rates (base_currency_id, target_currency_id, rate) values (1, 5, 469.88);
insert into exchange_rates (base_currency_id, target_currency_id, rate) values (1, 6, 0.81);
