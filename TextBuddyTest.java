import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class TextBuddyTest {
	static TextBuddy buddy ;
	@SuppressWarnings("static-access")
	@BeforeClass 
	public static void init() throws IOException{
		buddy = new TextBuddy("test.txt");
		buddy.fileName = "test.txt";
	}
	
	@SuppressWarnings("static-access")
	@After
	public void clearFile() throws IOException{
		buddy.clear();
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testAdd() throws IOException {
		assertEquals("Add Test","added to test.txt: "+'"'+"one little kid"+'"',buddy.addLine("add one little kid"));
		assertEquals("invalid add format. Correct format : add <line_to_add>",buddy.addLine("add"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testDelete() throws IOException {
		assertEquals("Add Test","added to test.txt: "+'"'+"one"+'"',buddy.addLine("add one"));
		assertEquals("Add Test","added to test.txt: "+'"'+"two"+'"',buddy.addLine("add two"));
		assertEquals("Add Test","added to test.txt: "+'"'+"three"+'"',buddy.addLine("add three"));
		assertEquals("Delete Test","deleted from test.txt: "+'"'+"one"+'"',buddy.deleteLine("delete 1"));
		assertEquals("Test after delete","1. two\r\n2. three",buddy.displayContent("test.txt"));
		assertEquals("Delete Invalid Line Test","line 4 does not exist or out of bound.",buddy.deleteLine("delete 4"));
		assertEquals("Invalid Delete input Test","invalid delete format. Correct format : delete <line_number>",buddy.deleteLine("delete"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testDisplay() throws IOException {
		assertEquals("Add Test","added to test.txt: "+'"'+"one"+'"',buddy.addLine("add one"));
		assertEquals("Add Test","added to test.txt: "+'"'+"two"+'"',buddy.addLine("add two"));
		assertEquals("Add Test","added to test.txt: "+'"'+"three"+'"',buddy.addLine("add three"));
		assertEquals("Display Test","1. one\r\n2. two\r\n3. three",buddy.displayContent("test.txt"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testSort() throws IOException {
		buddy.addLine("add one");
		buddy.addLine("add two");
		buddy.addLine("add three");
		assertEquals("Sort Test","file has been sorted",buddy.sort());
		assertEquals("Display after Sort","1. one\r\n2. three\r\n3. two",buddy.displayContent("test.txt"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testSearch() throws IOException {
		buddy.addLine("add This is test");
		buddy.addLine("add Search this");
		buddy.addLine("add Ignore");
		assertEquals("Invalid Search format test","invalid search format. Correct format : search <word_to_search>",buddy.search("search"));
	}

}
