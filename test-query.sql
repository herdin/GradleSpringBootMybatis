select * from test;


;

--
insert into tb_card
select
  '1040121031182730'
  , card_sta_cd
  , card_prd_id
  , 'herdin'
  , rgt_dtm
  , null, null
from tb_card where 1=1
and card_no = '1010000100010001'

;
select * from tb_card where 1=1
--and card_no = '1010010131588568'
and card_sta_cd = '00'
;
select * from tb_memcard