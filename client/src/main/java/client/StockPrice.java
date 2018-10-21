package client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {
	private String id;
	private String ticker;
	private double price;
	private Date when;
}
