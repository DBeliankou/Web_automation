package com.My;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;



public class Base
{
	protected static WebDriver driver = null;

	protected String path = "";
	protected String gecko = "";
	protected String chrome = "";
	private Properties prop = new Properties();

	// Switch between webbrowser's drivers and get values using DDT;
	protected WebDriver getDriver() throws Exception
	{
		prop.load(new FileInputStream(path));
		String browser = prop.getProperty("Webbrowser");
		switch (browser)
		{
		case "Firefox":
			System.setProperty("webdriver.gecko.driver", gecko);
			return new FirefoxDriver();
		case "Chrome":
			System.setProperty("webdriver.chrome.driver", chrome);
			return new ChromeDriver();
		}
		return null;
	}

	protected String getValue(String key) throws Exception
	{
		prop.load(new FileInputStream(path));
		String value = prop.getProperty(key);
		return value;
	}

	// Fluent wait;
	protected void waitForElementToBeClickable(WebElement element)
	{
		new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS).pollingEvery(2, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.until(ExpectedConditions.elementToBeClickable(element));
	}

	protected void clickElement(WebElement element)
	{
		waitForElementToBeClickable(element);
		element.click();
	}
	
	//Validation methods:
	
	public Base validateAttribute(String css, String attr, String regex)
	{
		return validateAttribute(By.cssSelector(css), attr, regex);
	}

	public Base validateAttribute(By by, String attr, String regex)
	{
		return validateAttribute(driver.findElement(by), attr, regex);
	}

	public Base validateAttribute(WebElement element, String attr, String regex)
	{
		String actual = null;
		try
		{
			actual = element.getAttribute(attr);
			if (actual.equals(regex))
			{
				return this; // test passes
			}
		}
		catch (Exception e)
		{
			Assertions.fail(String.format(
					"Attribute not fount! [Attribute: %s] [Desired value: %s] [Actual value: %s] [Element: %s] [Message: %s]",
					attr, regex, actual, element.toString(), e.getMessage()), e);
		}

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(actual);

		Assertions.assertThat(m.find())
		.withFailMessage(
				"Attribute doesn't match! [Attribute: %s] [Desired value: %s] [Actual value: %s] [Element: %s]",
				attr, regex, actual, element.toString())
		.isTrue();
return this;
	}
	
	//Validates text. Validate text ignoring white spaces;
	public Base validateText(String css, String text)
	{
		return validateText(By.cssSelector(css), text);
	}

	public Base validateText(By by, String text)
	{
		Assertions.assertThat(text).isEqualToIgnoringWhitespace(getText(by));
		return this;
	}

	public String getText(By by)
	{
		WebElement element = driver.findElement(by);
		return element.getTagName().equalsIgnoreCase("input") || element.getTagName().equalsIgnoreCase("select")
				|| element.getTagName().equalsIgnoreCase("textarea") ? element.getAttribute("value") : element.getText();
	}

	public String getText(WebElement element)
	{
		return element.getTagName().equalsIgnoreCase("input") || element.getTagName().equalsIgnoreCase("select")
				|| element.getTagName().equalsIgnoreCase("textarea") ? element.getAttribute("value") : element.getText();
	}
	
	//Validate the presence of element
	
	public Base validatePresent(String css)
	{
		return validatePresent(By.cssSelector(css));
	}

	public Base validatePresent(By by)
	{
		Assertions.assertThat(driver.findElements(by).size())
				.withFailMessage("Element not present: [Element: %s]", by.toString()).isGreaterThan(0);
		return this;
	}
}
