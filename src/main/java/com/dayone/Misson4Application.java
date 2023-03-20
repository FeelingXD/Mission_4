package com.dayone;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Misson4Application {

	public static void main(String[] args) {
//		SpringApplication.run(Misson4Application.class, args);
		try{
			Connection connection=Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=1616198400&period2=1647734400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
			Document document = connection.get();

			Elements eles =document.getElementsByAttributeValue("data-test","historical-prices");
			Element ele= eles.get(0);

			Element tbody = ele.children().get(1);
			for (Element e : tbody.children())  {
				String txt= e.text();
				if (!txt.endsWith("Dividend")){
					continue;
				}
				String[] splits = txt.split(" ");
				String month = splits[0];
				int day = Integer.valueOf(splits[1].replace(",",""));
				int year = Integer.valueOf(splits[2]);
				String dividend =splits[3];


				System.out.println(txt);

				System.out.println(year+"/"+month+"/" +day + "- > " +dividend);
			}

//			System.out.println(ele);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
