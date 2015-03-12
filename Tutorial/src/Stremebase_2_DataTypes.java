import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import com.stremebase.map.ObjectMap;
import com.stremebase.map.OneMap;
import com.stremebase.base.DB;
import com.stremebase.base.To;
import com.stremebase.base.util.Lexicon;


public class Stremebase_2_DataTypes
{
  public static OneMap map;

  private static final Scanner in = new Scanner(System.in);


  public static void main(String[] args)
  {
    DB.startDB(false);
    map = new OneMap("map");
    welcome();
    lesson1_integers();
    lesson2_booleans();
    lesson3_doubles();
    lesson4_temporalunits();
    lesson5_strings();
    lesson6_objectserialization();
    l();
    p("Case closed.");
    p("Next chapter is about multivalued attributes: arrays, lists and sets.");
    p("Keep going!");
  }

  public static void welcome()
  {
    l();
    p("Stremebase Tutorial - Chapter 2: Supported Data Types");
    p("");
    p("Under the hood Stremebase operates with longs (arrays of 64 bits).");
    p("Therefore any data type for which you can devise a bijection to longs is supported.");
    p("And if the bijection is order-preserving, even range queries will work correctly.");
    p("");
    p("For your convenience, there is a class called com.stremebase.base.To");
    p("It contains static functions for converting most common datatypes to longs and back.");
    p("Therefore following data types are supported out of the box:");
    p("");
    p("long: naturally - hence anything Java can cast to long: ints, shorts, bytes, ...");
    p("boolean: An array of booleans per entry");
    p("double: floating point numbers");
    p("java.time.Instant: with a millisecond precision");
    p("java.time.localDateTime: Instant with a zone information from DB.db.ZONE");
    p("java.lang.String: no size limits");
    p("java.lang.StringBuilder: full text documents can be indexed and searched (not covered here, but in chapter 4)");
    p("");
    p("If you cannot figure out how to map a datatype to long (some BLOB maybe),");
    p("you can create a map with serializable objects as values,");
    p("but then indexing is not supported.");
    p("");
    p("Without further ado, let's try this out in practice.");
    p("");
    p("(Press ENTER when ready...)");
    in.nextLine();
  }

  public static void lesson1_integers()
  {    
    l();
    p("1: INTEGER DATA TYPES");
    p("");
    p("Here are examples of how you handle integers (whole numbers): ");
    p("");
    p("long lv = 4564564565234l;");
    p("map.put(1, lv);");
    p("map.commit();");
    p("lv = map.get(1);");
    p("System.out.printf(\"%%d\", lv);");
    long lv = 4564564565234l;
    map.put(1, lv);
    map.commit();
    lv = map.get(1);
    System.out.printf("%d", lv);
    p("");
    p("");
    p("int iv = 22222222;");
    p("map.put(2, iv);");
    p("map.commit();");
    p("iv = map.get(2);");
    p("System.out.printf(\"%%d\", iv);");
    long iv = 22222222;
    map.put(2, iv);
    map.commit();
    iv = map.get(2);
    System.out.printf("%d", iv);
    p("");
    p("");
    p("short sv = -32333;");
    p("map.put(3, sv);");
    p("map.commit();");
    p("sv = (short) map.get(3);");
    p("System.out.printf(\"%%d\", sv);");
    short sv = -32333;
    map.put(3, sv);
    map.commit();
    sv = (short) map.get(3);
    System.out.printf("%d", sv);
    p("");
    p("");
    p("char cv = 'A';");
    p("map.put(4, cv);");
    p("map.commit();");
    p("cv = (char) map.get(4);");
    p("System.out.printf(\"%%c\", cv);");
    char cv = 'A';
    map.put(4, cv);
    map.commit();
    cv = (char) map.get(4);
    System.out.printf("%c", cv);
    p("");
    p("");
    p("byte bv = 55;");
    p("map.put(5, bv);");
    p("map.commit();");
    p("bv = (byte) map.get(5);");
    p("System.out.printf(\"%%d\", bv);");
    byte bv = 55;
    map.put(5, bv);
    map.commit();
    bv = (byte) map.get(5);
    System.out.printf("%d", bv);
    p("");
    p("(end of lesson 1)");
    in.nextLine();      
  }

  public static void lesson2_booleans()
  {
    l();
    p("2: BOOLEANS");
    p("");
    p("Because 64 bits is smallest addressable unit, we can handle 63 booleans in one shot.");
    p("(better not tamper with the bit at index 63)");
    p("");

    p("boolean b0 = true;");
    p("boolean b1 = false;");
    p("boolean b62 = true;");
    p("long booleanArray = To.l(b0, 0, 0);");
    p("booleanArray = To.l(b1, 1, booleanArray);");
    p("booleanArray = To.l(b62, 62, booleanArray);");
    p("map.put(1, booleanArray);");
    p("map.commit();");
    p("booleanArray = map.get(1);");
    p("b0 = To.toBoolean(booleanArray, 0);");
    p("b1 = To.toBoolean(booleanArray, 1);");
    p("b62 = To.toBoolean(booleanArray, 62);");
    p("System.out.printf(\"%%b, %%b, %%b\", b0, b1, b62);");

    map.clear();
    boolean b0 = true;
    boolean b1 = false;
    boolean b62 = true;
    long booleanArray = To.l(b0, 0, 0);
    booleanArray = To.l(b1, 1, booleanArray);
    booleanArray = To.l(b62, 62, booleanArray);
    map.put(1, booleanArray);
    map.commit();

    booleanArray = map.get(1);
    b0 = To.toBoolean(booleanArray, 0);
    b1 = To.toBoolean(booleanArray, 1);
    b62 = To.toBoolean(booleanArray, 62);
    System.out.printf("%b, %b, %b", b0, b1, b62);
    p("");
    p("");
    p("For these boolean arrays, range queries do not make sense.");
    p("Just scan through all values and apply a bit mask.");
    p("For example: to get keys where booleans at index 0 and 62 are true, you would: ");
    p("final long trueMask = To.mask(0, 62);");    
    p("map.keyset().filter(key -> ((map.get(key) & trueMask) == trueMask)).forEach(key -> p(\"%%d\", key));");
    final long trueMask = To.mask(0, 62);    
    map.keyset().filter(key -> ((map.get(key) & trueMask) == trueMask)).forEach(key -> p("%d", key));
    p("");
    p("And to get keys where the boolean at index 1 is false, you would: ");
    p("final long falseMask = To.mask(1);");    
    p("map.keyset().filter(key -> ((map.get(key) & falseMask) == 0)).forEach(key -> p(\"%%d\", key));");
    final long falseMask = To.mask(1);    
    map.keyset().filter(key -> ((map.get(key) & falseMask) == 0)).forEach(key -> p("%d", key));
    p("");
    p("(end of lesson 2)");
    in.nextLine();
  }

  public static void lesson3_doubles()
  {
    l();
    p("3: DOUBLES");
    p("");
    p("Just remember to convert doubles to longs with To.l() and back with To.toDouble()");
    p("");
    p("double dv = -12345.6789");
    p("map.put(1, To.l(dv));");
    p("map.put(2, To.l(2.9999999999));");
    p("map.put(3, To.l(3));");
    p("map.put(4, To.l(3.0000000001));");
    p("map.put(5, To.l(3.9999999999));");
    p("map.put(6, To.l(4));");
    p("map.put(7, To.l(4.0000000001));");
    p("map.commit();");
    p("dv = map.get(1);");
    p("System.out.printf(\"%%f\", dv);");
    double dv = -12345.6789;
    map.put(1, To.l(dv));
    map.put(2, To.l(2.9999999999));
    map.put(3, To.l(3));
    map.put(4, To.l(3.0000000001));
    map.put(5, To.l(3.9999999999));
    map.put(6, To.l(4));
    map.put(7, To.l(4.0000000001));
    map.commit();
    dv = To.toDouble(map.get(1));
    System.out.printf("%f", dv);
    p("");
    p("");
    p("Also queries work as expected: ");
    p("map.query(To.l(3), To.l(4)).forEach(k -> System.out.printf(\"%%d -> %%f.10f\", k, To.toDouble(map.get(k))));");
    map.query(To.l(3), To.l(4)).forEach(k -> p("%d -> %.10f", k, To.toDouble(map.get(k))));
    p("");
    p("(end of lesson 3)");
    in.nextLine();
  }

  public static void lesson4_temporalunits()
  {
    l();
    p("4: TEMPORAL UNITS");
    p("");
    p("Stremebase supports java.time.Instant with a millisecond precision.");
    p("You can also use java.time.localDateTime.");
    p("Zone information for localDateTime comes from DB.db.ZONE, which is currently: "+DB.db.ZONE);
    p("");

    p("Instants go by: ");
    p("for (long key = 0; key < 10; key++) map.put(key, To.l(Instant.now()));");
    p("map.commit();");
    p("for (long key = 0; key < 10; key++) System.out.println(To.instant(map.get(key)).toString());");    

    for (long key = 0; key < 10; key++) map.put(key, To.l(Instant.now()));
    map.commit();
    for (long key = 0; key < 10; key++) System.out.println(To.instant(map.get(key)).toString());

    in.nextLine(); 
    p("");
    p("localDateTimes go by: ");
    p("for (long key = 0; key < 100; key++) map.put(key, To.l(LocalDateTime.now()));");
    p("map.commit();");
    p("for (long key = 0; key < 100; key++) System.out.println(To.localDateTime(map.get(key)).toString());");    

    for (long key = 0; key < 100; key++) map.put(key, To.l(LocalDateTime.now()));
    map.commit();
    for (long key = 0; key < 100; key++) System.out.println(To.localDateTime(map.get(key)).toString());

    p("");
    map.clear();
    p("");
    p("(end of lesson 4)");
    in.nextLine();
  }

  public static void lesson5_strings()
  {
    map.clear();

    l();
    p("5: STRINGS");
    p("");
    p("In Stremebase strings are either words (terms, tokens) or texts (arrays of words).");
    p("At this point we'll handle words, texts will be discussed in chapter 4.");
    p("All strings are globally handled with com.stremebase.base.util.Lexicon");
    p("It offers static methods for accessing the lexicon (examples below)");
    p("");
    p("String handling is case-sensitive, therefore convert all strings to lower (or upper) case.");
    p("");
    p("Putting strings is straightforward:");
    p("map.put(1, To.l(\"smith\"));");
    p("map.put(2, To.l(\"smithson\"));");
    p("map.put(3, To.l(\"smithers\"));");
    p("DB.db.commit();");
    map.put(1, To.l("smith"));
    map.put(2, To.l(")/(%¤#&/(&"));
    map.put(3, To.l("smithson"));
    map.put(4, To.l("smirgeli"));
    map.put(5, To.l("smithers"));
    DB.db.commit();
    p("");  
    System.out.println("You can get the value as String, which is convenient but slow: ");
    p("System.out.println(\"1 -> \"+To.toString(map.get(1)));");

    System.out.println("1 -> "+To.toString(map.get(1)));

    p("");
    System.out.println("Or, you can get the value to your StringBuilder: ");
    p("StringBuilder string = new StringBuilder();");
    p("To.toString(map.get(1), string);");
    p("System.out.println(string.toString());");

    StringBuilder string = new StringBuilder();
    To.toString(map.get(1), string);
    System.out.println(string.toString());

    p("");
    p("The conversion from strings to longs does not preserve alphabetical order.");
    p("Therefore query ranges do not make sense.");
    p("But you can still search for particular strings: ");
    p("long sl = Lexicon.getIfExists(\"smith\");");
    p("if (sl!=DB.NULL) map.query(sl, sl).forEach(key -> (System.out.println(key)));");

    long sl = Lexicon.getIfExists("smith");
    if (sl!=DB.NULL) map.query(sl, sl).forEach(key -> (p("%d", key)));

    p("");
    p("Furthermore, you can find all keys that contain string values that start with some string prefix.");
    p("To find all keys with value starting with 'smit': ");
    p("map.unionQuery(TextMap.wordsWithPrefix(\"smit\").toArray()).forEach(key -> (System.out.printf(\"%%d -> %%s%%n\", key, To.toString(map.get(key)))));");
    map.unionQuery(Lexicon.wordsWithPrefix("smit").toArray()).forEach(key -> (p("%d -> %s", key, To.toString(map.get(key)))));
    p("");
    p("(end of lesson 5)");
    in.nextLine();
  }

  public static void lesson6_objectserialization()
  {
    l();
    p("6: OBJECT SERIALIZATION");
    p("");
    p("Any serializable object can be persisted.");
    p("But objects cannot be indexed.");
    p("");
    p("So far we have used OneMap; for objects, we'll use ObjectMap.");
    p("");
    p("");
    p("ExampleClass example = new ExampleClass();");
    p("example.wave = new byte[44100]; // we shall persist sound");
    p("...");
    p("ObjectMap objectMap = new ObjectMap(\"objectMap\");");
    p("objectMap.put(1, example);");
    p("objectMap.commit();");
    p("ExampleClass serializedObject = (ExampleClass) objectMap.get(1);");
    p("serializedObject.playSound();");
    p("");

    class ExampleClass implements Serializable
    {
      private static final long serialVersionUID = 1L;
      byte[] wave;

      public void playSound()
      {
        AudioFormat af = new AudioFormat( 44100, 8, 1, true, false);
        SourceDataLine sdl;
        try
        {
          sdl = AudioSystem.getSourceDataLine( af );
          sdl.open();
        }
        catch (Exception e)
        {
          p("Audio not supported.");
          in.nextLine();
          return;
        }
        sdl.start();
        sdl.write(wave, 0, wave.length);
        sdl.drain();
        sdl.stop();
      }
    }

    ExampleClass example = new ExampleClass();
    example.wave = new byte[44100];  //we shall persist sound
    for( int i = 0; i < 44100; i++ )
    {
      double angle = i / ( (float )44100 / 440 ) * 2.0 * Math.PI;
      example.wave[i] = (byte )( Math.sin( angle ) * 100 );
    }

    ObjectMap objectMap = new ObjectMap("objectMap");
    objectMap.put(1, example);
    objectMap.commit();
    ExampleClass serializedObject = (ExampleClass) objectMap.get(1);
    serializedObject.playSound();

    in.nextLine();
    p("");
    p("(end of lesson 6)");
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
