package com.example.demoes;

import com.example.demoes.entity.Product;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

//@Component
public class Crawler {
    public static List<Product> data = new ArrayList<>();

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.addArguments("lang=en");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        Map<String, List<String>> urlMap = new HashMap<>();
        urlMap.put("dien-thoai", Arrays.asList("https://www.thegioididong.com/dtdd#c=42&o=9&pi=5", " item ajaxed __cate_42"));
        urlMap.put("laptop", Arrays.asList("https://www.thegioididong.com/laptop#c=44&o=9&pi=14", " item  __cate_44"));
        urlMap.put("may-tinh-bang", Arrays.asList("https://www.thegioididong.com/may-tinh-bang#c=522&o=9&pi=1", " item ajaxed __cate_522"));

        for (String key : urlMap.keySet()) {
            List<Product> subData = new ArrayList<>();
            while (subData.size() <= 20) {
                subData.clear();
                driver.get(urlMap.get(key).get(0));
                Document doc = Jsoup.parse(driver.getPageSource());
                Elements products = doc.getElementsByClass(urlMap.get(key).get(1));
                System.out.println(products.size());
                products.forEach(p -> {
                    subData.add(new Product(p.getElementsByTag("a").attr("data-id"),
                            p.getElementsByTag("a").attr("data-name"),
                            Double.parseDouble(p.getElementsByTag("a").attr("data-price")),
                            p.getElementsByTag("a").attr("data-brand"),
                            p.getElementsByTag("a").attr("data-cate")
                    ));});
            }
            data.addAll(subData);

        }

        System.out.println(data.size());

//        driver.get("https://www.thegioididong.com/laptop#c=44&o=9&pi=14");
//
//        // HtmlPage page = webClient.getPage("https://www.thegioididong.com/dtdd#c=42&o=9&pi=5");
//        // webClient.waitForBackgroundJavaScript(5000); // important! wait until javascript finishes rendering
//
//        Document doc = Jsoup.parse(driver.getPageSource());
//        Elements products = doc.getElementsByClass(" item  __cate_44");
//        System.out.println(products.size());
//        products.forEach(p -> {
//          data.add(new Product(p.getElementsByTag("a").attr("data-id"),
//                  p.getElementsByTag("a").attr("data-name"),
//                  Double.parseDouble(p.getElementsByTag("a").attr("data-price")),
//                  p.getElementsByTag("a").attr("data-brand"),
//                  p.getElementsByTag("a").attr("data-cate")
//                  ));});

    }
}
