package com.sp.productmanagement.core.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productLookup")
public class ProductLookupEntity implements Serializable {

	private static final long serialVersionUID = 4676683253274533409L;

	@Id
	private String productId;

	@Column(unique = true)
	private String title;

}
