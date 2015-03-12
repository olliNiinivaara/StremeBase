import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import com.stremebase.base.DB;
import com.stremebase.base.To;
import com.stremebase.base.util.TextIndex;


public class Stremebase_4_TextRetrieval
{
  private static final Scanner in = new Scanner(System.in);

  public static void main(String[] args)
  {
    DB.startDB(true);
    welcome();
    example();
    l();
    p("End of chapter.");
    p("Chapter 5 will be all about speed: indices");
  }

  public static void welcome()
  {
    l();
    p("Stremebase Tutorial - Chapter 4: Text Retrieval");
    p("");
    p("In chapter 2 (lesson 5) words were being converted to longs and back.");
    p("In this chapter we query documents by words using com.stremebase.base.util.TextIndex");
    p("");
    p("TextIndex offers you an inverted index, it does not store the texts themselves.");
    p("If you need to store the documents also, you have three options:");
    p("1: Store only the addresses (URL, file path), like in the example below");
    p("2: Store the documents to ObjectMap");
    p("3: Store the word order to a ListMap");
    p("");
    p("TextIndex query results are ordered by document keys (oldest document first).");
    p("");
    p("(Press ENTER to do some Text Retrieval...)");
    in.nextLine();
  }

  public static void example()
  {
    TextIndex textIndex = new TextIndex("textIndex", true);

    l();
    p("TEXT RETRIEVAL EXAMPLE");
    if (!DB.db.exists())
    {
      p("");
      p("Enter path to a directory with lots of .txt files: ");
      String path = "/home/olli/Data/rfc";//in.nextLine();
      String[] files = new File(path).list();
      if (files==null)
      {
        p(path+" is not a directory.");
        p("");
        System.exit(1);
      }

      index(textIndex, path, files);
      textIndex.commit();
    }
    query(textIndex);
    //textIndex.clear();
  }

  public static void index(TextIndex textIndex, String path, String[] files)
  {
    p("indexing (wait)...");
    int docs = 0;
    for (String name: files)
    {      
      if (!name.endsWith(".txt")) continue;
      File file = new File(path+File.separator+name);
      try
      {
        java.nio.file.Files
        .lines(Paths.get(file.toURI()), Charset.defaultCharset())
        .flatMap(line -> Arrays.stream(line.split(" +")))
        .forEach(w ->
        {
          w = w.replaceAll("[^\\p{L}\\p{Nd}]+", "").toLowerCase();
          if (w.length()>1) textIndex.add(w, To.l(file.getAbsolutePath()));
        });
      }
      catch (Exception e)
      {
        p(name+ " skipped - "+e.getClass().getName());
        continue;
      }
      docs++;
      p(docs+": "+name);
      if (docs==1500) break;
    }
    p(docs+" documents indexed.");
  }

  public static void query(TextIndex textIndex)
  {
    String query;
    p("");
    p("Enter search words.");
    p("Multiple words are ANDed.");
    p("To perform a multiple character wildcard search use the \"*\" symbol at the end of a word.");
    p("Empty query finishes.");
    while (true)
    {
      System.out.print("Query: ");
      query = in.nextLine();
      if (query.isEmpty()) break;
      textIndex.query(query.split(" +")).forEach(d -> p("%d %s", d, To.toString(d)));
      p("---");
    }
  }

  private static void p(String format, Object... args)
  {
    System.out.printf(format+"%n", args);
  }

  private static void l()
  {
    System.out.println("\n---------------------------------------------------------------");
  }
}
