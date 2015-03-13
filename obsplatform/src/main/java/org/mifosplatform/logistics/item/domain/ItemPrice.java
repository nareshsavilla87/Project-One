package org.mifosplatform.logistics.item.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_item_price", uniqueConstraints = { @UniqueConstraint(columnNames = { "item_id", "region_id", "price" }, name = "itemid_with_region_uniquekey") })
public class ItemPrice extends AbstractPersistable<Long>{

	@Column(name = "region_id")
	private Long regionId;

	@Column(name = "price")
	private BigDecimal price;
	
	@ManyToOne
    @JoinColumn(name="item_id", insertable = true, updatable = true, nullable = false, unique = true)
	private ItemMaster itemMaster;
	
	public ItemPrice(){}

	public ItemPrice(Long regionId, BigDecimal price) {
		
		this.regionId = regionId;
		this.price = price;
	}

	public void update(ItemMaster itemMaster) {
		
		this.itemMaster = itemMaster;
	}
	

}