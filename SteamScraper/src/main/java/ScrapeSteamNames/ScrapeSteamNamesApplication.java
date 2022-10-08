package ScrapeSteamNames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootApplication
public class ScrapeSteamNamesApplication {

	public static void main(String[] args) {
		// SpringApplication.run(ScrapeSteamNamesApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ScrapeSteamNamesApplication.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);
		// RestApi();
		// viaMarket();
		// getAllSKinnames();
		// Test();
		/*
		 * try { Selenium(); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		SteamPriceCollector steam = new SteamPriceCollector();

		steam.getItemPrice("https://csgostash.com/agent/62/Jungle-Rebel-Elite-Crew");
		steam.getItemPrice("https://csgostash.com/pin/27/Inferno-2-Pin");
		steam.getItemPrice("https://csgostash.com/sticker/294/Knife-Club");
		steam.getItemPrice("https://csgostash.com/item/10286/Spectrum-2-Case-Key");
		steam.getItemPrice("https://csgostash.com/patch/24/Copper-Lambda");
		steam.getItemPrice("https://csgostash.com/graffiti/66/X-Knives/Jungle-Green");
		//steam.getItemPrice("https://csgostash.com/music/34/Backbone-Roam");
		//Musickit
		steam.getCasePrice("https://csgostash.com/case/307/Fracture-Case");
		steam.getCasePrice("https://csgostash.com/stickers/capsule/312/2020-RMR-Challengers");
		steam.getCasePrice("https://csgostash.com/stickers/capsule/194/Autograph-Capsule-Natus-Vincere-Atlanta-2017");
		steam.getCasePrice("https://csgostash.com/case/355/Recoil-Case");
		steam.getCasePrice("https://csgostash.com/patches/pack/296/CS:GO-Patch-Pack");
		steam.stop();
		/*
		 * HashMap<String, Double> test =
		 * steam.getSkinprices("https://csgostash.com/skin/1213/Skeleton-Knife-Fade");
		 * 
		 * System.out.println(test.get("SFactoryNew"));
		 * System.out.println(test.get("SMinimalWear"));
		 * System.out.println(test.get("SFieldTested"));
		 * System.out.println(test.get("SWellWorn"));
		 * System.out.println(test.get("SBattleScarred"));
		 * System.out.println(test.get("FactoryNew"));
		 * System.out.println(test.get("MinimalWear"));
		 * System.out.println(test.get("FieldTested"));
		 * System.out.println(test.get("WellWorn"));
		 * System.out.println(test.get("BattleScarred"));
		 * System.out.println(test.get("S"));
		 */

		System.out.println("Fertig");

	}

	public static void Test() {

	}

	static String auth = null;
	static boolean pressed = false;

	public static void viaMarket() {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(15));
		String url = "https://steamcommunity.com/market/search?appid=730";
		driver.get("https://steamcommunity.com/market/search?appid=730");

		ArrayList<JSONObject> n = getAllSKinnames();
		JSONArray namen = (JSONArray) parseObject(new File("C://Users//timle//Desktop//Steam//AllSkinnames.txt"))
				.get("yes");

		for (int i = 0; i < namen.size(); i++) {

			String current = (String) namen.get(i);
			current = normalisieren(current);

			String neueUrl = "https://steamcommunity.com/market/search?appid=730&q=" + current;

			System.out.println(neueUrl);

			driver.get(neueUrl);

			boolean ergebnis = false;
			WebElement priceelement;
			WebElement search;
			try {
				WebElement waitsearch = wait.until(ExpectedConditions.elementToBeClickable(By.id("result_0")));
				search = driver.findElement(By.id("result_0"));
				ergebnis = true;
			} catch (Exception e) {

				driver.navigate().refresh();

				WebElement waitsearch = wait2.until(ExpectedConditions.elementToBeClickable(By.id("result_0")));
				search = driver.findElement(By.id("result_0"));
				ergebnis = true;
			}

			if (ergebnis) {

				priceelement = search.findElement(By.className("normal_price"));
				String priceString = priceelement.getText();
				priceString = priceString.replaceAll("$", "");
				priceString = priceString.replaceAll("USD", "");
				priceString = priceString.replaceAll("Ab:", "");
				priceString = priceString.replaceAll("\n", "");
				priceString = priceString.replaceAll(" ", "");
				priceString = priceString.substring(1);

				Double price = Double.parseDouble(priceString);
				System.out.println(price);

			}

		}

	}

	public static void Selenium() throws InterruptedException {

		WebDriverManager.chromedriver().setup();
		// Durch WebDriverManager unnötig
		// System.setProperty("webdriver.chrome.driver",
		// "D:\\Progarmmierstuff\\Selenium\\chromedriver.exe");

		// options.addArguments("--headless");

		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		driver.get("https://csgostash.com/");

		ArrayList<JSONObject> n = getAllSKinnames();
		JSONArray namen = (JSONArray) parseObject(new File("C://Users//timle//Desktop//Steam//AllSkinnames.txt"))
				.get("yes");

		JSONArray fertig = new JSONArray();

		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div[2]/button[2]")));
		WebElement cookie = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div[2]/button[2]"));
		cookie.click();

		String prevurl = "yeet";
		String url = null;
		for (int i = 8249; i < n.size(); i++) {
			try {
				String hashname = (String) n.get(i).get("marketHashName");
				String name = (String) n.get(i).get("normalisierterName");

				System.out.println(hashname);
				JSONObject tmp = new JSONObject();
				tmp.put("normalisierterName", name);
				tmp.put("marketHashName", normalisieren2(hashname));

				WebElement element1 = wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath("/html/body/div[2]/div[2]/nav/div/div[2]/form/div/div/span[1]/input")));

				WebElement search = driver
						.findElement(By.xpath("/html/body/div[2]/div[2]/nav/div/div[2]/form/div/div/span[1]/input"));
				search.sendKeys(name);

				WebElement element2 = wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath("/html/body/div[2]/div[2]/nav/div/div[2]/form/div/div[2]/ul/li[2]")));
				WebElement suggestion = driver
						.findElement(By.xpath("/html/body/div[2]/div[2]/nav/div/div[2]/form/div/div[2]/ul/li[2]"));
				suggestion.click();

				url = driver.getCurrentUrl();

				if (prevurl.equals(url)) {

					driver.navigate().refresh();
					i = i - 1;
					Thread.sleep(3000);
					System.out.println("trfresh");
					prevurl = "yes";
					continue;
				}

				/*
				 * if (hashname.contains("Sticker")) {
				 * 
				 * if ((!hashname.contains("(Gold)") && !hashname.contains("(Holo)") &&
				 * !hashname.contains("(Glitter)")) && (url.contains("-Gold-") ||
				 * url.contains("-Holo-") || url.contains("-Glitter-") ||
				 * url.contains("-Foil-"))) {
				 * 
				 * int index = url.indexOf("/sticker/") + 9; String start =
				 * url.substring(index); System.out.println(start); int index2 =
				 * start.indexOf("/");
				 * 
				 * String zahl = (String) start.substring(0, index2); int z =
				 * Integer.parseInt(zahl); System.out.println(z);
				 * 
				 * if (url.contains("-Gold-")) { z-=3; } else if (url.contains("-Holo-")) {
				 * 
				 * } else if (url.contains("-Glitter-")) {
				 * 
				 * } else if (url.contains("-Foil-")) {
				 * 
				 * }
				 * 
				 * }
				 * 
				 * }
				 */

				if (url.contains("https://csgostash.com/graffiti")) {

					if (hashname.contains(" (Battle Green)")) {
						url = url + "/Battle-Green";
					} else if (hashname.contains(" (Bazooka Pink)")) {

						url = url + "/Bazooka-Pink";
					} else if (hashname.contains(" (Blood Red)")) {

						url = url + "/Blood-Red";
					} else if (hashname.contains(" (Brick Red)")) {

						url = url + "/Brick-Red";
					} else if (hashname.contains(" (Cash Green)")) {

						url = url + "/Cash-Green";
					} else if (hashname.contains(" (Desert Amber)")) {

						url = url + "/Desert-Amber";
					} else if (hashname.contains(" (Dust Brown)")) {

						url = url + "/Dust-Brown";
					} else if (hashname.contains(" (Frog Green)")) {

						url = url + "/Frog-Green";
					} else if (hashname.contains(" (Jungle Green)")) {

						url = url + "/Jungle-Green";
					} else if (hashname.contains(" (Monarch Blue)")) {

						url = url + "/Monarch-Blue";
					} else if (hashname.contains(" (Monster Purple)")) {

						url = url + "/Monster-Purple";
					} else if (hashname.contains(" (Princess Pink)")) {

						url = url + "/Princess-Pink";
					} else if (hashname.contains(" (SWAT Blue)")) {

						url = url + "/SWAT-Blue";
					} else if (hashname.contains(" (Shark White)")) {

						url = url + "/Shark-White";
					} else if (hashname.contains(" (Tiger Orange)")) {

						url = url + "/Tiger-Orange";
					} else if (hashname.contains(" (Tracer Yellow)")) {

						url = url + "/Tracer-Yellow";
					} else if (hashname.contains(" (Violent Violet)")) {

						url = url + "/Violent-Violet";
					} else if (hashname.contains(" (War Pig Pink)")) {

						url = url + "/War-Pig-Pink";
					} else if (hashname.contains(" (Wire Blue)")) {

						url = url + "/Wire-Blue";
					}

				}

				tmp.put("URL", url);
				prevurl = url;

				System.out.println(i + "/" + n.size() + " " + tmp);

				fertig.add(tmp);
				tmp.put("index", i);
				write(tmp, new File("C://Users//timle//Desktop//Steam//AktuelleSkinPreise.txt"));

			} catch (Exception e) {
				// System.out.println(e.getMessage());
				e.printStackTrace();
				// driver.navigate().refresh();
				// driver.get(url);
				// Thread.sleep(5000);
				i = i - 1;
				continue;

			}
		}

		JSONObject duh = new JSONObject();
		// duh.put("yes", fertig);
		// write(duh, new
		// File("C://Users//timle//Desktop//Steam//AktuelleSkinPreise.txt"));

		System.out.println("Fertig");
		driver.quit();

	}

	public static ArrayList<JSONObject> getAllSKinnames() {

		File f = new File("C://Users//timle//Desktop//Steam//AllSkinnames.txt");

		JSONObject o = parseObject(f);
		JSONArray ar = (JSONArray) o.get("yes");

		ArrayList<JSONObject> erg = new ArrayList<>();
		for (int i = 0; i < ar.size(); i++) {

			String s = (String) ar.get(i);

			s = s.replaceAll(" \\(Battle-Scarred\\)", "");
			s = s.replaceAll(" \\(Factory New\\)", "");
			s = s.replaceAll(" \\(Field-Tested\\)", "");
			s = s.replaceAll(" \\(Minimal Wear\\)", "");
			s = s.replaceAll(" \\(Well-Worn\\)", "");
			s = s.replaceAll("StatTrak™ ", "");
			s = s.replaceAll("Souvenir ", "");

			JSONObject tmp = new JSONObject();
			tmp.put("marketHashName", s);

			s = s.replaceAll("Music Kit \\| ", "");
			s = s.replaceAll("\\\\u0026#39", "\'");
			s = s.replaceAll("%27", "\'");
			s = s.replaceAll("\\(Holo\\-Foil\\)", "\\(Holo\\/Foil\\)");
			s = s.replaceAll("\\\\u0026", "\\&");
			s = s.replaceAll("Patch \\| ", "");
			s = s.replaceAll("Sealed Graffiti \\| ", "");
			s = s.replaceAll("Sticker \\| ", "");
			s = s.replaceAll(" \\(Battle Green\\)", "");
			s = s.replaceAll(" \\(Blood Red\\)", "");
			s = s.replaceAll(" \\(Bazooka Pink\\)", "");
			s = s.replaceAll(" \\(Brick Red\\)", "");
			s = s.replaceAll(" \\(Cash Green\\)", "");
			s = s.replaceAll(" \\(Desert Amber\\)", "");
			s = s.replaceAll(" \\(Dust Brown\\)", "");
			s = s.replaceAll(" \\(Frog Green\\)", "");
			s = s.replaceAll(" \\(Jungle Green\\)", "");
			s = s.replaceAll(" \\(Monarch Blue\\)", "");
			s = s.replaceAll(" \\(Monster Purple\\)", "");
			s = s.replaceAll(" \\(Princess Pink\\)", "");
			s = s.replaceAll(" \\(SWAT Blue\\)", "");
			s = s.replaceAll(" \\(Shark White\\)", "");
			s = s.replaceAll(" \\(Tiger Orange\\)", "");
			s = s.replaceAll(" \\(Tracer Yellow\\)", "");
			s = s.replaceAll(" \\(Violent Violet\\)", "");
			s = s.replaceAll(" \\(War Pig Pink\\)", "");
			s = s.replaceAll(" \\(Wire Blue\\)", "");
			s = s.replaceAll(" \\(Foil\\)", " Foil");
			s = s.replaceAll(" \\(Holo\\)", " Holo");
			s = s.replaceAll(" \\(Gold\\)", " Gold");
			s = s.replaceAll(" \\(Glitter\\)", " Glitter");
			s = s.replaceAll("★", "");

			tmp.put("normalisierterName", s);

			boolean yes = false;
			for (int a = 0; a < erg.size(); a++) {
				if (erg.get(a).containsValue(tmp.get("marketHashName")))
					yes = true;

			}
			if (!yes)
				erg.add(tmp);
		}

		for (int i = 0; i < erg.size(); i++) {

			System.out.println(erg.get(i));
		}

		return erg;

	}

	public static void RestApi() {
		System.out.println("StartTime: " + getDate());
		JSONObject yes = parseObject(new File("C://Users//timle//Desktop//Steam//AllSkinnames.txt"));
		JSONArray arr = (JSONArray) yes.get("yes");
		JSONArray alles = new JSONArray();
		RestTemplate restTemplate = new RestTemplate();

		URI u = null;

		for (int i = 0; i < arr.size(); i++) {
			try {

				String normalisiert = normalisieren((String) arr.get(i));
				String normal = (String) arr.get(i);
				String uri = "https://steamcommunity.com/market/priceoverview/?appid=730&currency=3&market_hash_name="
						+ normalisiert;

				u = new URI(uri);

				JSONObject s = restTemplate.getForObject(u, JSONObject.class);

				s.put("marketHashName", normal);
				alles.add(s);

				System.out.println(i + "/" + arr.size() + ": " + s.toJSONString());

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
				try {
					System.out.println("sleep");
					Thread.sleep(3600000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				i = i - 1;
				continue;

			}
		}
		JSONObject erg = new JSONObject();
		erg.put("Datum", getDate());
		erg.put("alles", alles);

		String ergebnis = toPrettyFormat(erg.toJSONString());
//
		File f = new File("C://Users//timle//Desktop//Steam//AktuelleSkinPreise.txt");
		write(erg, f);

	}

	public static void write(JSONObject s, File f) {

		FileWriter myWriter;
		JSONObject tmp = new JSONObject();
		JSONObject yes = new JSONObject();

		if (f.length() != 0)
			tmp = parseObject(f);

		JSONArray arr2 = (JSONArray) tmp.get("yes");
		JSONArray arr = new JSONArray();
		if (arr2 != null)
			for (int i = 0; i < arr2.size(); i++)
				arr.add(arr2.get(i));

		arr.add(s);
		yes.put("yes", arr);
		try {
			myWriter = new FileWriter(f, false);
			BufferedWriter bw = new BufferedWriter(myWriter);
			bw.write(toPrettyFormat(yes.toJSONString()));
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static String getDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		Date date = new Date(System.currentTimeMillis());
		String ergebnis = (formatter.format(date));

		System.out.println("Uhrzeit: " + ergebnis);
		return ergebnis;
	}

	public static String normalisieren(String s) {

		s = s.replaceAll(" ", "%20");
		s = s.replaceAll("\\|", "%7C");
		s = s.replaceAll("\\\\u0026#39", "'");

		URLEncoder.encode(s, StandardCharsets.UTF_8);

		return s;

	}

	public static String normalisieren2(String s) {

		s = s.replaceAll(" \\(Battle-Scarred\\)", "");
		s = s.replaceAll(" \\(Factory New\\)", "");
		s = s.replaceAll(" \\(Field-Tested\\)", "");
		s = s.replaceAll(" \\(Minimal Wear\\)", "");
		s = s.replaceAll(" \\(Well-Worn\\)", "");
		s = s.replaceAll("StatTrak™ ", "");
		s = s.replaceAll("Souvenir ", "");

		return s;

	}

	public static JSONObject parseObject(File f) {

		JSONParser parser = new JSONParser();

		Object obj = null;

		try {
			obj = parser.parse(new FileReader(f));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject wert = (JSONObject) obj;

		return wert;

	}

	public static String toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}
}
