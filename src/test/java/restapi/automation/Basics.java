package restapi.automation;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import pojoClasses.Api;
import pojoClasses.GetCoursesParent;
import pojoClasses.WebAutomation;


public class Basics {
	public static void main(String[] args) throws Exception {

		// Step 1 - Getting the Authorization code (OTP)

		/*
		 * Mandatory fields for GetAuthorization Code Request: End point: Authorization
		 * server URL Query Params: Scope, Auth_url, client_id, response_type,
		 * redirect_uri 
		 * This operation should perform on browser and not on postman
		 * output: Code
		 */
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\hemant.madan\\Downloads\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
//		driver.get("https://accounts.google.com/signin/oauth/oauthchooseaccount?client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&as=-GHFpqexGhqSjLpgVmNpxQ&destination=https%3A%2F%2Frahulshettyacademy.com&approval_state=!ChRmd1lSczdVNnJ5ci1PTjVTbzU4ORIfQTFwVXN5V1RLQndaOEhuU1JuY2dubW9Pbmx6ZDZCWQ%E2%88%99AJDr988AAAAAXdfN3KQaN7J9pY2QxT7FInX_qwGpg-lU&oauthgdpr=1&xsrfsig=ChkAeAh8TwnoDJw0lF4UioO0AByHNZNhCV-lEg5hcHByb3ZhbF9zdGF0ZRILZGVzdGluYXRpb24SBXNvYWN1Eg9vYXV0aHJpc2t5c2NvcGU&flowName=GeneralOAuthFlow");
		driver.get("https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php");
		WebElement emailID=driver.findElement(By.cssSelector("input#identifierId"));
		emailID.sendKeys("hrdocs799@gmail.com");
		emailID.sendKeys(Keys.ENTER);
		Thread.sleep(3000);
		WebElement pwd=driver.findElement(By.xpath("//input[@type='password']"));
		pwd.sendKeys("hrdocs799");
		pwd.sendKeys(Keys.ENTER);
		Thread.sleep(4000);
		String url=driver.getCurrentUrl();
		
		String partialCode=url.split("code=")[1];
		String code=partialCode.split("&scope")[0];
		System.out.println("Authorization code is:: "+code);
		
		
		// Step 2 - Getting the access token

		/*
		 * Mandatory fields for GetAccessToken Request: End point: Access token URL
		 * Query Params: code, client_id, client secret, redirect_uri, grant_type
		 * 
		 * Output: Access token
		 */

		String accessTokenResponse = given().urlEncodingEnabled(false)
				.queryParams("code", code)
				.queryParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.queryParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
				.queryParams("redirect_uri", "https://rahulshettyacademy.com/getCourse.php")
				.queryParams("grant_type", "authorization_code").when().log().all()
				.post("https://www.googleapis.com/oauth2/v4/token").asString();
		JsonPath js = new JsonPath(accessTokenResponse);
		String accessT = js.getString("access_token");

		
		
		// Step 3 - Sending the actual request after getting the access token

		/*
		 * String resp = given().queryParam("access_token", accessT).when().log().all()
		 * .get("https://rahulshettyacademy.com/getCourse.php").asString();
		 * System.out.println(resp);
		 */
		
		// Step 3 - Sending the actual request after getting the access token

				GetCoursesParent  gc = given().queryParam("access_token", accessT).expect().defaultParser(Parser.JSON)
						.when()
						.get("https://rahulshettyacademy.com/getCourse.php").as(GetCoursesParent.class);
				System.out.println("my linkedin profile is :::"+gc.getLinkedIn());
				System.out.println("my Instructor is :::"+gc.getInstructor());
				System.out.println(gc.getCourses().getApi().get(1).getCourseTitle());
				List<Api> apiCources=gc.getCourses().getApi();
				
				for(int i=0;i<apiCources.size();i++)
				{
					if (apiCources.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing"))
						System.out.println(apiCources.get(i).getPrice());
				}
				
				List<WebAutomation> webAutomationCources=gc.getCourses().getWebAutomation();
				for(int i=0;i<webAutomationCources.size();i++)
				{
//					if (webAutomationCources.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing"))
						System.out.println(webAutomationCources.get(i).getCourseTitle());
				}
	}
}
