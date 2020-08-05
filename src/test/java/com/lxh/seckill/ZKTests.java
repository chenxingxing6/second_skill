package com.lxh.seckill;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.dao.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ZKTests {
	@Autowired
	GoodsMapper goodsMapper;

	@Test
	public void test01() throws SQLException {

	}

	@Test
	public void test02(){
		List<GoodsBo> goodsBos = goodsMapper.selectAllGoodes();
		for (GoodsBo goodsBo : goodsBos){
			log.info(goodsBo+"");
		}
	}
}
