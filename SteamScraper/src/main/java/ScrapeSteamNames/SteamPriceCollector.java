package ScrapeSteamNames;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SteamPriceCollector {

	private WebDriver driver;
	private WebDriverWait wait;
	private ChromeOptions chromeOptions;

	public SteamPriceCollector() {

		// Bilder werden weggelassen
		// Nimmt immer die aktuellste treiberversion
		// Patcht den Cookiebutton am anfang raus
		WebDriverManager.chromedriver().setup();

		chromeOptions = new ChromeOptions();
		HashMap<String, Object> images = new HashMap<String, Object>();
		images.put("images", 2);
		HashMap<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_setting_values", images);
		chromeOptions.setExperimentalOption("prefs", prefs);
		driver = new ChromeDriver(chromeOptions);
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));

		driver.get("https://csgostash.com/");

		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div[2]/button[2]")));
		WebElement cookie = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div[2]/button[2]"));
		cookie.click();

	}

	/**
	 * 
	 * Funktioniert für:
	 * 
	 * -agents -pins -sticker -spezielle Items -patches -Grafitties
	 * 
	 * @param url
	 * @return
	 */
	public Double getItemPrice(String url) {
		try {
			// Zieht sich den Preis von CSGOStash
			driver.get(url);

			wait.until(ExpectedConditions
					.elementToBeClickable(By.cssSelector("div[class='btn-group btn-group-justified']")));

			WebElement button = driver.findElement(By.cssSelector("div[class='btn-group btn-group-justified']"));
			WebElement priceElemet = button.findElement(By.className("pull-right"));
			String priceString = priceElemet.getAttribute("innerHTML");

			if (priceString.equals("No Recent Price on Steam") || priceString.equals("No Recent Price")) {

				return null;

			}

			// Formattiert den gefundenen String
			priceString = priceString.replaceAll("€", "");
			priceString = priceString.replaceAll(",", ".");
			priceString = priceString.replaceAll("-", "");
			Double price = Double.parseDouble(priceString);
			System.out.println(price);

			return price;
		} catch (Exception e) {
			e.printStackTrace();
			return getItemPrice(url);

		}

	}

	/**
	 * 
	 * Gibt eine HashMap mit Zustand und Preis des Skins zurück
	 *
	 * Parameter S als Zustand -1.0 ->StatTrak -2.0 ->Souvenier -3.0 ->Nichts
	 * 
	 * @param url
	 * @return
	 */

	public HashMap<String, Double> getSkinprices(String url) {

		HashMap<String, Double> ergebnis = new HashMap<>();
		driver.get(url);
		wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("div[class='btn-group-sm btn-group-justified']")));

		List<WebElement> elemente = driver
				.findElements((By.cssSelector("div[class='btn-group-sm btn-group-justified']")));

		for (int i = 1; i <= elemente.size() / 2; i++) {

			WebElement priceElemet = elemente.get(i).findElement(By.className("pull-right"));
			String priceString = priceElemet.getAttribute("innerHTML");

			if (priceString.equals("No Recent Price") || priceString.equals("Not Possible")) {

				continue;
			}

			// Formattiert den gefundenen String
			priceString = priceString.replaceAll("€", "");
			priceString = priceString.replaceAll(",", ".");
			priceString = priceString.replaceAll("-", "");
			priceString = priceString.replaceAll(" ", "");
			Double price = Double.parseDouble(priceString);

			switch (i) {

			case 1:
				ergebnis.put("SFactoryNew", price);
				break;
			case 2:
				ergebnis.put("SMinimalWear", price);
				break;
			case 3:
				ergebnis.put("SFieldTested", price);
				break;
			case 4:
				ergebnis.put("SWellWorn", price);
				break;
			case 5:
				ergebnis.put("SBattleScarred", price);
				break;
			case 6:
				ergebnis.put("FactoryNew", price);
				break;
			case 7:
				ergebnis.put("MinimalWear", price);
				break;
			case 8:
				ergebnis.put("FieldTested", price);
				break;
			case 9:
				ergebnis.put("WellWorn", price);
				break;
			case 10:
				ergebnis.put("BattleScarred", price);
				break;

			}

		}
		try {
			WebElement StattTrak = elemente.get(1)
					.findElement(By.cssSelector("span[class='pull-left price-details-st']"));
			String condition = StattTrak.getAttribute("innerHTML");

			// Prüft ob ein SKin Souvenier, StatTrak oder nichts ist

			if (condition.equals("StatTrak")) {

				ergebnis.put("S", 1.0);

			} else if (condition.equals("Souvenir")) {

				ergebnis.put("S", 2.0);

			}
		} catch (Exception e) {

			ergebnis.put("S", 3.0);
			// Räumt auf
			ergebnis.put("FactoryNew", ergebnis.get("SFactoryNew"));
			ergebnis.remove("SFactoryNew");
			ergebnis.put("MinimalWear", ergebnis.get("SMinimalWear"));
			ergebnis.remove("SMinimalWear");
			ergebnis.put("FieldTested", ergebnis.get("SFieldTested"));
			ergebnis.remove("SFieldTested");
			ergebnis.put("WellWorn", ergebnis.get("SWellWorn"));
			ergebnis.remove("SWellWorn");
			ergebnis.put("BattleScarred", ergebnis.get("SBattleScarred"));
			ergebnis.remove("SBattleScarred");
		}

		return ergebnis;
	}

	/**
	 * Funktioniert für
	 * 
	 * -Cases 
	 * -capsules
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public Double getCasePrice(String url) {
		
		driver.get(url);
		
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("/html/body/div[3]/div[2]/div/div[1]/div/a[1]")));
		
		WebElement button = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div/div[1]/div/a[1]"));
		String s = button.getAttribute("innerHTML");
		
		if(s.equals("No Recent Price on Steam") ||s.equals("0")) {
			
			return null;
		}
		
		s = s.replaceAll("€", "");
		s = s.replaceAll("-", "");
		s = s.replaceAll("on Steam", "");
		s = s.replaceAll(" ", "");
		s = s.replaceAll(",", ".");
		
		Double price = Double.parseDouble(s);
		System.out.println(price);
		
		
		return price;

	}

	/**
	 * schließt alles
	 */
	public void stop() {

		// driver.close();
		driver.quit();

	}
}
