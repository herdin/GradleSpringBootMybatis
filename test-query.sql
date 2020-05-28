select * from test;


;

--
insert into tb_card
select
  '1010001731089502'
  , card_sta_cd
  , card_prd_id
  , 'herdin'
  , rgt_dtm
  , null, null
from tb_card where 1=1
and card_no = '1010000100010001'

;
select * from tb_card tc where 1=1
--and tc.card_no = '1010010131588568'
and tc.rgsr_id = 'herdin'
and tc.card_sta_cd = '00'
and not exists (
    select 1 from tb_memcard tm where 1=1
    and tc.card_no = tm.card_no
)
;
select * from tb_memcard