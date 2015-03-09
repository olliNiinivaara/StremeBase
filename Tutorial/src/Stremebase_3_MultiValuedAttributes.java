import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;

import com.stremebase.base.DB;
import com.stremebase.base.To;
import com.stremebase.map.ArrayMap;
import com.stremebase.map.ListMap;
import com.stremebase.map.SetMap;


public class Stremebase_3_MultiValuedAttributes
{
  private static final Scanner in = new Scanner(System.in);

  public static void main(String[] args)
  {
    DB.startDB(false);
    welcome();
    lesson1_arraymap();
    lesson2_listmap();
    lesson3_setmap();
    lesson4_attributedmap();
    l();
    p("That's all for now.");
    p("Tutorial continues with Chapter 4: Text Retrieval");
    p("See you there!");
  }

  public static void welcome()
  {
    l();
    p("Stremebase Tutorial - Chapter 3: Multivalued Attributes");
    p("");
    p("OneMap, which has already been introduced, associates one value with one key.");
    p("In this chapter we get familiar with maps supporting more than one value per key: ");
    p("ArrayMap: A fixed number of values per key");
    p("ListMap: An unlimited number of values per key");
    p("SetMap: Set of values per key, ordered by value, supports multisets or one arbitrary value per value");
    p("");
    p("SetMap in particular is very powerful - and it is the primary way to store relationships ('foreign keys') between entities");
    p("");
    p("(Press ENTER when ready...)");
    in.nextLine();
  }

  public static void lesson1_arraymap()
  {
    l();
    p("1: ARRAYMAP");
    p("");
    p("Arraymap lets you associate a fixed number of values per key.");
    p("Storing all non-queryable values to same arraymap is a very efficient trick.");
    p("However, querying an arrayMap would only make sense when the values are of the same type.");
    p("");
    p("Here is an example of how to pack many values to one arraymap: ");

    p("Creation of new arrayMap for four attributes: ArrayMap arrayMap = new ArrayMap(\"arraymap\", 4, false);");
    ArrayMap arrayMap = new ArrayMap("arraymap", 4, false);
    p("Names for the attributes: final short NAME = 0, SOMEVALUE = 1, TIMESTAMP = 2, ODDITY = 3;");
    p("(1st value will be a word, 2nd a long, 3rd an Instant and 4th a long)");
    final short NAME = 0, SOMEVALUE = 1, TIMESTAMP = 2, ODDITY = 3;
    p("A placeholder for values: final long[] values = new long[4]");
    final long[] values = new long[4];
    p("Putting 10 values: ");
    p("for (int key = 0; key < 10; key++)");
    p("{");
    p("  values[NAME] = To.l(\"key\"+key);");
    p("  values[SOMEVALUE] = key;");
    p("  values[TIMESTAMP] = To.l(Instant.now());");
    p("  values[ODDITY] = key %% 2 == 0 ? 0 : 1;");
    p("  arrayMap.put(key, values);");
    p("}");
    for (int key = 0; key < 10; key++)
    {
      values[NAME] = To.l("key"+key);
      values[SOMEVALUE] = key;
      values[TIMESTAMP] = To.l(Instant.now());
      values[ODDITY] = key % 2 == 0 ? 0 : 1;
      arrayMap.put(key, values);
    }
    p("arrayMap.commit();");
    arrayMap.commit();
    p("");
    p("Now we can loop through the arraymap ");
    p("and print entries where ODDITY-attribute is 1: ");
    p("arrayMap.keys().forEach(key ->");
    p("{");
    p(" arrayMap.get(key, values);");
    p(" if (values[ODDITY]==1) System.out.printf(\"Entity '%%s' has some odd value %%d and a timestamp %%s%%n\",");
    p("     To.toString(values[NAME]), values[SOMEVALUE], To.instant(values[TIMESTAMP]).toString()");
    p("");
    p("});");
    arrayMap.keys().forEach(key ->
    {
      arrayMap.get(key, values);
      if (values[ODDITY]==1) System.out.printf("Entity '%s' has some odd value %d and a timestamp %s%n",
          To.toString(values[NAME]), values[SOMEVALUE], To.instant(values[TIMESTAMP]).toString());
    });
    p("");
    p("(end of lesson 1)");
    in.nextLine();
  }

  public static void lesson2_listmap()
  {
    l();
    p("2: LISTMAP");
    p("");
    p("Listmap lets you associate an unlimited number of values per key.");
    p("It is much slower than ArrayMap, therefore always consider ArrayMap when the number of values is limited");
    p("This is more like a stack than a true list data structure, because you cannot insert or remove between nodes.");
    p("Instead of removing, you can overwrite with DB.NULL and thereafter iterators will skip it.");
    p("");
    p("An example follows (commits omitted, you remember to call them...): ");
    p("");
    p("Creating a new list: ListMap list = new ListMap(\"list\");");
    ListMap list = new ListMap("list");
    p("Pushing 100000 values to key 0: for (int i = 0; i<100000; i++) list.push(0, i);");
    for (int i = 0; i<100000; i++) list.push(0, i);
    list.commit();
    System.out.println("Tail of list 0: "+list.getTailPosition(0));
    p("Popping 50000 values from list of key 0:  for (int i = 0; i<50000; i++) list.pop(0);");
    for (int i = 0; i<50000; i++) list.pop(0);
    list.commit();
    System.out.println("Tail of list 0: "+list.getTailPosition(0));

    p("Removing every other value in list: for (int i = 0; i<50000; i+=2) list.put(0, i, DB.NULL);");
    for (int i = 0; i<50000; i+=2) list.put(0, i, DB.NULL);
    list.commit();
    p("List of all keys that contain value 9 in their list: ");
    list.unionQuery(9).forEach(key -> System.out.printf("A list at key %d contains value 9%n", key));
    p("List of all keys that contain value 10 in their list (none expected, because 10 was previously removed): ");
    list.unionQuery(10).forEach(key -> System.out.printf("A list at key %d contains value 10%n", key));
    p("Let's put value 10 to 0's list to index 27855: list.put(0, 27855, 10);");
    list.put(0, 27855, 10);
    list.commit();
    p("");
    p("New attempt to list of all keys that are associated with value 10:"); 
    list.unionQuery(10).forEach(key -> System.out.printf("A list at key %d contains value 10%n", key));
    p("");
    p("(end of lesson 2)");
    in.nextLine();
  }

  public static void lesson3_setmap()
  {
    l();
    p("3: SETMAP");
    p("");
    p("Setmap is the most versatile of the maps.");
    p("it can be used as a set, orderded set, multiset, and a set with a value associated with a value");
    p("Let's start with an example of a basic set: ");

    p("Terse way to construct a SetMap.SET: SetMap set = new SetMap(\"set\");");
    SetMap set = new SetMap("set");
    p("Putting entries to key 1, including duplicates, in an arbitrary order: ");
    p("set.put(1, 500);");
    set.put(1, 500);
    p("set.put(1, 500);");
    set.put(1, 500);
    p("set.put(1, 200);");
    set.put(1, 200);
    p("set.put(1, 300);");
    set.put(1, 300);
    p("set.put(1, 200);");
    set.put(1, 200);
    p("Committing before reading is critical with SetMaps, because modifications go to a separate cache: set.commit();");
    set.commit();
    p("Note how you get the values in sorted order: ");
    p("set.values(1).forEach(value ->System.out.printf(\"%%d%%n\", value));");
    set.values(1).forEach(value -> p("%d", value));
    p("Sortedness means that we can get fast set intersections with stremebase.base.util.Streams.intersection");
    p("");
    p("Next we remove one entry: set.remove(1, 200);");
    set.remove(1, 200);
    p("set.commit();");
    set.commit();
    p("And it's gone: set.values(1).forEach(value ->System.out.printf(\"%%d%%n\", value));");
    set.values(1).forEach(value -> p("%d", value));
    p("");
    set.clear();

    p("");
    p("This time with a SetMap.MULTISET flag: ");
    p("SetMap multiSet = new SetMap(\"multiset\", 10, SetMap.MULTISET, false);");
    SetMap multiSet = new SetMap("multiset", 10, SetMap.MULTISET, false);
    p("multiSet.put(1, 500);");
    multiSet.put(1, 500);
    p("multiSet.put(1, 500);");
    multiSet.put(1, 500);
    p("multiSet.put(1, 200);");
    multiSet.put(1, 200);
    p("multiSet.put(1, 300);");
    multiSet.put(1, 300);
    p("multiSet.put(1, 200);");
    multiSet.put(1, 200);
    p("multiSet.commit();");
    multiSet.commit();
    p("Because we want to see also the counts of values, we stream SetMap.Entries, not just values:");    
    p("multiSet.entries(1).forEach(entry -> System.out.printf(\"value %%d appears %%d times\", entry.value , entry.attribute));");
    multiSet.entries(1).forEach(entry -> p("value %d appears %d times", entry.value , entry.attribute));
    p("");
    p("multiSet.remove(1, 500);");
    multiSet.remove(1, 500);
    p("multiSet.remove(1, 300);");
    multiSet.remove(1, 300);
    p("multiSet.remove(1, 300);");
    multiSet.remove(1, 300);
    p("multiSet.commit();");

    multiSet.commit();
    p("Because we removed 300 twice from single entry, there is now negative amount of 300-values: ");
    p("multiSet.entries(1).forEach(entry -> System.out.printf(\"value %%d appears %%d times\", entry.value , entry.attribute));");
    multiSet.entries(1).forEach(entry -> p("value %d appears %d times", entry.value , entry.attribute));
    p("");
    p("(end of lesson 3)");
    in.nextLine();
  }

  public static void lesson4_attributedmap()
  {
    l();
    p("4: ATTRIBUTEDMAP");
    p("");
    p("SetMap.ATTRIBUTEDMAP is a map where you can associate an arbitrary attribute tag with each value");
    p("");
    p("When you store relationships, the attribute could contain, for example,");
    p("the type of the relationship, the weight of the relationship,");
    p("or the key of the relationship so that you can associate more attributes to it.");
    p("");
    p("We shall solve a travelling salesman problem with a greedy algorithm.");
    p("We store the distance between cities as the attribute.");
    p("Consult source code about details of the map and the algorithm.");
    p("");

    /*
               m
               a
               a                           h
               s       h   s               u
               t   a   e   i   g           l
               i   c   r   t   l   e   b   b   a       e
               c   h   l   a   e   c   o   e   n   o   p
               h   e   e   r   e   h   n   r   n   h   e
               t   n   n   d   n   t   n   g   e   e   n
    maastricht 0   29  20  21  16  31  100 12  4   31  18
        aachen 29  0   15  29  28  40  72  21  29  41  12
       heerlen 20  15  0   15  14  25  81  9   23  27  13
       sittard 21  29  15  0   4   12  92  12  25  13  25
        geleen 16  28  14  4   0   16  94  9   20  16  22
          echt 31  40  25  12  16  0   95  24  36  3   37
          bonn 100 72  81  92  94  95  0   90  101 99  84
      hulsberg 12  21  9   12  9   24  90  0   15  25  13
         kanne 4   29  23  25  20  36  101 15  0   35  18
           ohe 31  41  27  13  16  3   99  25  35  0   38
          epen 18  12  13  25  22  37  84  13  18  38  0

    Optimal:
    maastricht -> hulsberg -> geleen -> sittard -> ohe -> echt
    -> heerlen -> bonn -> aachen -> epen -> kanne -> maastricht
     */

    SetMap map = new SetMap("map", 10, SetMap.ATTRIBUTEDSET, false);
    map.put(new long[][] {{0, 1, 29}, {0, 2, 20}, {0, 3, 21}, {0, 4, 16}, {0, 5, 31}, {0, 6, 100}, {0, 7, 12}, {0, 8, 4}, {0, 9, 21}, {0, 10, 18}});
    map.put(new long[][] {{1, 0, 29}, {1, 2, 15}, {1, 3, 29}, {1, 4, 28}, {1, 5, 40}, {1, 6, 72}, {1, 7, 21}, {1, 8, 29}, {1, 9, 41}, {1, 10, 12}});
    map.put(new long[][] {{2, 0, 20}, {2, 1, 15}, {2, 3, 15}, {2, 4, 14}, {2, 5, 25}, {2, 6, 81}, {2, 7, 9}, {2, 8, 23}, {2, 9, 27}, {2, 10, 13}});
    map.put(new long[][] {{3, 0, 21}, {3, 1, 29}, {3, 2, 15}, {3, 4, 4}, {3, 5, 12}, {3, 6, 92}, {3, 7, 12}, {3, 8, 25}, {3, 9, 13}, {3, 10, 25}});
    map.put(new long[][] {{4, 0, 16}, {4, 1, 28}, {4, 2, 14}, {4, 3, 4}, {4, 5, 16}, {4, 6, 94}, {4, 7, 9}, {4, 8, 20}, {4, 9, 16}, {4, 10, 22}});
    map.put(new long[][] {{5, 0, 31}, {5, 1, 40}, {5, 2, 25}, {5, 3, 12}, {5, 4, 16}, {5, 6, 95}, {5, 7, 24}, {5, 8, 36}, {5, 9, 3}, {5, 10, 37}});
    map.put(new long[][] {{6, 0, 100}, {6, 1, 72}, {6, 2, 81}, {6, 3, 92}, {6, 4, 94}, {6, 5, 95}, {6, 7, 90}, {6, 8, 101}, {6, 9, 99}, {6, 10, 84}});
    map.put(new long[][] {{7, 0, 12}, {7, 1, 21}, {7, 2, 9}, {7, 3, 12}, {7, 4, 9}, {7, 5, 24}, {7, 6, 90}, {7, 8, 15}, {7, 9, 25}, {7, 10, 13}});
    map.put(new long[][] {{8, 0, 4}, {8, 1, 29}, {8, 2,23 }, {8, 3, 25}, {8, 4, 20}, {8, 5, 36}, {8, 6, 101}, {8, 7, 15}, {8, 9,35 }, {8, 10, 18}});
    map.put(new long[][] {{9, 0, 31}, {9, 1, 41}, {9, 2, 27}, {9, 3, 13}, {9, 4, 16}, {9, 5, 3}, {9, 6, 99}, {9, 7, 25}, {9, 8, 35}, {9, 10, 38}});
    map.put(new long[][] {{10, 0, 18}, {10, 1, 12}, {10, 2, 13}, {10, 3, 25}, {10, 4, 22}, {10, 5, 37}, {10, 6, 84}, {10, 7, 13}, {10, 8, 18}, {10, 9, 38}});
    map.commit();

    long[] greedy = greedySalesman(map);
    p("Greedy solution: "+Arrays.toString(greedy));
    long distance = 0;
    for (int i = 0; i<11; i++) distance+= map.getAttribute(greedy[i], greedy[i+1]);
    p("Distance of greedy solution: "+distance);
    p("");
    p("Optimal solution would have been: 0-7-4-3-9-5-2-6-1-10-8-0");
    p("Optimal solution's distance is 253");

    p("");
    p("(end of lesson 4)");
  }

  public static long[] greedySalesman(SetMap map)
  {
    long[] route = new long[12];
    route[0] = 0;

    boolean[] visited = new boolean[11];
    long visit = 1;

    while (visit<11)
    {
      long nextCity = -1;
      long nextDistance = Long.MAX_VALUE;

      for (int i=1; i<11; i++)
      {
        if (visited[i]) continue;
        long d = map.getAttribute(route[(int) (visit-1)], i);

        if (d<nextDistance)
        {
          nextCity = i;
          nextDistance = d;
        }     
      } 
      route[(int) visit] = nextCity;
      visited[(int) nextCity] = true;
      visit++;
    }
    route[11] = 0;
    return route;
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
