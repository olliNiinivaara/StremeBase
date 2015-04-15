import java.util.Scanner;
import java.util.stream.Collectors;

import com.stremebase.base.DB;
import com.stremebase.map.ArrayMap;
import com.stremebase.map.ListMap;
import com.stremebase.map.OneMap;
import com.stremebase.map.SetMap;


public class Stremebase_5_Indices
{
  private static final Scanner in = new Scanner(System.in);

  public static void main(String[] args)
  {
    DB.startDB(false);
    welcome();
    lesson1_one_to_one();
    lesson2_many_to_one();
    lesson3_one_to_many();
    lesson4_many_to_many();
    lesson5_many_to_multimany();
    l();
    p("END OF TUTORIAL"); 
    p("Now you have learned the basic features of Stremebase.");
    p("Please consult JavaDoc and source code for further reference.");
  }

  public static void welcome()
  {
    l();
    p("Stremebase Tutorial - Chapter 5: Indices");
    p("");
    p("Queries (and unionQueries) iterate through all keys and values to select those that match.");
    p("If this iteration takes too long, you should consider adding an index to the map.");
    p("Index is a map from values back to keys, considerably improving query (and unionQuery) performance.");
    p("But indices are not free: Insertions and removals become much slower, ");
    p("and indices need a lot of space.");  
    p("");
    p("There are five types of indices: ");
    p("One-to-One: Every key is associated with at most one value and vice versa.");
    p("Many-to-One: Every key is associated with at most one value.");
    p("One-to-Many: Every value is associated with at most one key at most once.");
    p("Many-to-Many: Every key is associated with any value at most once");
    p("Many-to-MultiMany: No restrictions on associations");
    p("");
    p("The more restricted type you can use, the faster the index.");
    p("Stremebase does not check for you that you obey the stated integrity constraints");
    p("");
    p("(Press ENTER for some speed comparisons...)");
    in.nextLine();
  }

  public static void lesson1_one_to_one()
  {
    l();
    p("1: ONE-TO-ONE");
    p("");
    p("One-to-one is the simplest (most restricted) index type.");
    p("It is meant for indexing a OneMap or one cell of an ArrayMap.");
    p("");

    p("Here is a simple comparison of query speeds with and without a one-to-one index:");
    p("");

    final long[] foo = new long[1];
    long start;

    OneMap oneMap = new OneMap("onemap");

    long without_insert;
    p("1000000 inserts without an index (wait)...");
    System.gc();
    start = System.currentTimeMillis();    
    for (int i=0; i<1000000; i++) oneMap.put(i, i-500000);
    oneMap.commit();
    without_insert = System.currentTimeMillis()-start;
    p("time: "+without_insert+" ms");

    long without_query;
    p("200 queries without an index (wait)...");
    System.gc();
    start = System.currentTimeMillis();    
    for (int i=0; i<200; i++) oneMap.unionQuery(i).forEach(key -> foo[0]+=key);
    without_query = System.currentTimeMillis()-start;
    p("time: "+without_query+" ms");

    long indextime;
    p("");
    p("Add an index...");
    System.gc();
    start = System.currentTimeMillis();    
    oneMap.addIndex(DB.ONE_TO_ONE);
    indextime = System.currentTimeMillis()-start;
    p("time: "+indextime+" ms");

    long with_query;
    foo[0] = 0;
    p("200 queries with an index...");
    System.gc();
    start = System.currentTimeMillis();    
    for (int i=0; i<200; i++) oneMap.unionQuery(i).forEach(key -> foo[0]+=key);
    with_query = System.currentTimeMillis()-start;
    p("time: "+(with_query)+" ms");

    p("");
    p("So, querying  with an index was about "+(without_query/with_query)+  " times faster! ");
    p("");
    p("A little trick: if you don't need indexed query results sorted by key (no query intersections),");
    p("you can tune performance by calling map.setIndexQueryIsSorted(false).");
    p("One-To-One and One-To-Many queries are then sorted by value, which may also be useful sometimes.");
    p("");

    p("(end of lesson 1)");
    in.nextLine();
  }

  public static void lesson2_many_to_one()
  {
    l();
    p("2: MANY-TO-ONE");
    p("");
    p("Many-to-one is the other index type that works only with a OneMap or a single cell of an ArrayMap.");
    p("It allows many keys to be associated with the same value.");
    p("");

    p("Here is a simple comparison of insertion and removal speeds with and without a many-to-one index:");
    p("");

    long start;

    ArrayMap without = new ArrayMap("manytoone_without", 5);
    long without_insert;
    long without_remove;

    System.gc();
    p("1000000 inserts without an index (wait)...");
    start = System.currentTimeMillis(); 
    for (int i=0; i<1000000; i++) for (int j=0; j<5; j++) without.put(i, j, i%100);
    without.commit();
    without_insert = System.currentTimeMillis()-start;
    p("time: "+(without_insert)+" ms");

    p("500000 removes without an index (wait)...");
    start = System.currentTimeMillis();    
    for (int i=0; i<500000; i++) without.remove(i);
    without.commit();
    without_remove = System.currentTimeMillis()-start;
    p("time: "+(without_remove)+" ms");

    without.clear();

    ArrayMap with = new ArrayMap("manytoone_with", 5);
    with.addIndextoCell(DB.MANY_TO_ONE, 3); //Here we attach an index to ArrayMap cell at index 3!!
    long with_insert;
    long with_remove;

    p("");
    System.gc();
    p("1000000 inserts with an index (wait)...");
    start = System.currentTimeMillis();    
    for (int i=0; i<1000000; i++) for (int j=0; j<5; j++) with.put(i, j, i%100);
    with.commit();
    with_insert = System.currentTimeMillis()-start;
    p("time: "+with_insert+" ms");

    p("500000 removes with an index (wait)...");
    start = System.currentTimeMillis();    
    for (int i=0; i<500000; i++) with.remove(i);
    with.commit();
    with_remove = System.currentTimeMillis()-start;
    p("time: "+(with_remove)+" ms");

    with.clear();

    if (without_insert == 0) without_insert = 1;
    if (without_remove == 0) without_remove = 1;

    p("");
    p("So, inserting with an index was about "+(with_insert/without_insert)+" times slower, ");
    p("and removing  with an index was about "+(with_remove/without_remove)+" times slower. ");
    p("");
    p("(end of lesson 2)");
    in.nextLine();
  }

  public static void lesson3_one_to_many()
  {
    l();
    p("3: ONE-TO-MANY");
    p("");
    p("One-to-Many is meant to index ArrayMaps, ListMaps and SetMaps,");
    p("where one key is associated with many values, but");
    p("one value is associated with one key at most once.");
    p("");
    p("If same value is repeated multiple times for a key, you have to use Many-to-MultiMany.");
    p("(Because one-to-many removals are greedy - if you never remove associations, it works)");
    p("");
    p("Here is a simple speed test for one-to-many:");
    p("");

    long start;

    ListMap listmap = new ListMap("listmap");
    listmap.addIndex(DB.ONE_TO_MANY);
    long listmap_insert;
    long listmap_query;

    System.gc();
    p("100000 * 100 inserts with a one-to-many index (wait)...");
    start = System.currentTimeMillis(); 
    for (long i=1; i<=100000; i++)
    { 
      for (int j=0; j<=99; j++) listmap.put(i, j, i*100+j);
    }
    listmap.commit();

    listmap_insert = System.currentTimeMillis()-start;
    p("time: "+(listmap_insert)+" ms");

    System.gc();
    p("");
    p("Querying all keys where a value larger than 8 million exists...");
    start = System.currentTimeMillis();

    long count = listmap.query(8000001, Long.MAX_VALUE).distinct().count();
    listmap_query = System.currentTimeMillis()-start;

    p(count + " keys found.");
    p("time: "+(listmap_query)+" ms");

    listmap.clear();

    p("");
    p("(end of lesson 3)");
    in.nextLine();
  }

  public static void lesson4_many_to_many()
  {
    l();
    p("4: MANY-TO-MANY");
    p("");
    p("Many-to-Many is meant to index ArrayMaps, ListMaps and SetMaps,");
    p("where one key is associated with many values, and");
    p("one value may be associated with many keys, but");
    p("not more than once.");
    p("");
    p("Here is a simple speed test for many-to-many:");
    p("");

    long start;

    SetMap setmap = new SetMap("setmap");
    setmap.addIndex(DB.MANY_TO_MANY);
    long setmap_insert;
    long setmap_query;

    System.gc();
    p("10000 * 1000 inserts with a many-to-many -index (wait)...");
    start = System.currentTimeMillis(); 
    for (long i=0; i<10000; i++)
    { 
      for (int j=0; j<1000; j++) setmap.put(i, j);
    }
    setmap.commit();
    setmap_insert = System.currentTimeMillis()-start;
    p("time: "+(setmap_insert)+" ms");

    p("");
    System.gc();
    p("Querying all keys where value 1 exists...");

    start = System.currentTimeMillis();
    long count = setmap.unionQuery(1).count();
    setmap_query = System.currentTimeMillis()-start;

    p(count + " keys found.");
    p("time: "+(setmap_query)+" ms");

    setmap.clear();

    p("");
    p("(end of lesson 4)");
    in.nextLine();
  }

  public static void lesson5_many_to_multimany()
  {
    l();
    p("5: MANY-TO-MULTIMANY");
    p("");
    p("Many-to-MultiMany is an index backed by a MultiSet.");
    p("It allows you to index arrays and lists where same value may be");
    p("associated with the same key multiple times.");
    p("");
    p("As stated in previous lesson, if you never remove values (or remove them at once),");
    p("you can use the more efficient MANY-TO-MANY index.");
    p("");
    p("Many-to-MultiMany index also supports filtering queries by value counts: ");
    p("You can also state how many times the value must appear.");
    p("");
    p("An example follows: ");
    p("");

    ListMap multilistmap = new ListMap("multilistmap");
    multilistmap.addIndex(DB.MANY_TO_MULTIMANY);

    for (long i=0; i<10; i++)
    { 
      for (int j=0; j<10; j++) multilistmap.push(i, (i+j)%6);
    }
    multilistmap.put(0, 0, DB.NULL);
    multilistmap.put(0, 6, DB.NULL);
    multilistmap.put(10, 0, new long[] {0,0,0,0});
    multilistmap.commit();

    p("");
    p("The contents of a listmap: ");
    multilistmap.keys().forEach(key->
    p(key+": "+multilistmap.values(key).mapToObj(value -> {return value+"";}).collect(Collectors.joining(", "))));
    p("");
    p("All keys where value 0 exists:");
    p(multilistmap.unionQuery(0).mapToObj(value -> {return value+"";}).collect(Collectors.joining(", ")));
    p("");
    p("All entries where value 0 exists exactly twice:");
    multilistmap.indexUnionQuery(new long[] {0}, 2, 2).forEach(entry->p(entry.toString()));

    multilistmap.clear();

    p("");
    p("(end of lesson 5)");
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
