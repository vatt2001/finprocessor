# --- !Ups

create table purse (
  id bigserial not null,
  type_id smallint not null,
  name varchar(255) not null,
  code varchar(16) not null,
  ordering smallint not null
);

create table purse_balance (
  id bigserial not null primary key,
  purse_id long not null,
  date_at date not null,
  ordering integer not null,
  amount long not null,
  currency_id char(3) not null,
  is_confirmed bool not null,

  constraint fk_purse_balance__purse foreign key (purse_id) references purse(id)
    on update cascade on delete restrict
);

create table transaction_type (
  id bigserial not null,
  parent_id long,
  name varchar(255) not null,
  code varchar(16) not null,
  ordering smallint not null,

  constraint fk_transaction_type__transaction_type foreign key (parent_id) references transaction_type(id)
    on update cascade on delete restrict
);

create table "transaction" (
  id bigserial not null primary key,
  type_id long not null,
  date_at date not null,
  ordering integer not null,
  created_at timestamptz not null,
  src_purse_id long not null,
  dst_purse_id long,
  amount long not null,
  currency_id char(3) not null,
  comment varchar(1024) not null,

  constraint fk_transaction__transaction_type foreign key (type_id) references transaction_type(id)
    on update cascade on delete restrict,

  constraint fk_transaction__src_purse foreign key (src_purse_id) references purse(id)
    on update cascade on delete restrict,

  constraint fk_transaction__dst_purse foreign key (dst_purse_id) references purse(id)
    on update cascade on delete restrict
);

-- DATA

insert into purse
  (id, type_id, name, code, ordering)
values
  (1, 1, 'Доходы', 'income', 1),
  (2, 1, 'Расходы', 'income', 2),
  (10, 2, 'Кошелек', 'к', 10),
  (11, 2, 'Зарплата', 'кЗП', 11),
  (12, 2, 'Яндекс-Деньги', 'сЯД', 12),
  (13, 2, 'Счет ПСБ', 'сПСБ', 13),
  (14, 2, 'Счет АБ', 'сАБ', 14),
  (15, 2, 'Зарплата (2)', 'кЗП2', 15),
  (16, 2, 'Белый конверт 3', 'кБ3', 16),
  (100, 3, 'Красный кошелек', 'кК', 20),
  (500, 4, 'Брокерский счет в Риком', 'сКриком', 500)
;

insert into transaction_type
  (id, parent_id, name, code, ordering)
values
  (10, null, 'Продукты', 'продукты', 10),
  (20, null, 'Хозяйство', 'хозяйство', 20),
  (30, null, 'Отдых', 'отдых', 30),
  (35, null, 'Подарки', 'подарки', 35),
  (40, null, 'Машина', 'машина', 40),
  (41, 40, 'Бензин', 'бензин', 41),
  (42, 40, 'Обслуживание', 'обслуживание', 42),
  (43, 40, 'Другое', 'другое', 43),
  (50, null, 'Транспорт', 'транспорт', 50),
  (60, null, 'ЕИ', 'ЕИ', 60),
  (70, null, 'Малыши', 'малыши', 70),
  (80, null, 'Здоровье', 'здоровье', 80),
  (81, 80, 'Лекарства', 'лекарства', 81),
  (82, 80, 'Лечение', 'лечение', 82),
  (90, null, 'Коммуналка', 'коммуналка', 90),
  (91, 90, 'Электроэнергия', 'электроэнергия', 91),
  (92, 90, 'Квартплата', 'квартплата', 92),
  (93, 90, 'Телефон', 'телефон', 92),
  (95, null, 'Еда', 'еда', 90),
  (100, null, 'Мобильный', 'мобильный', 100),
  (500, null, 'Зарплата', 'ЗП', 500),
  (900, null, 'Другое', 'другое', 900),
  (1000, null, 'Перевод', '-', 1000)
;


# --- !Downs

drop table purse;

drop table purse_balance;

drop table transaction_type;

drop table "transaction";
