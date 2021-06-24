
import java.util.concurrent.ExecutionException;

public class Main {
      public static void main(String[] args) throws ExecutionException, InterruptedException {
          final String path = "";

          var someClass = new SomeClass();
          var resultMap = someClass.startAsync(
                  path + "папка в которой искать",
                  "искать файлы в которых содержится это слово",
                  path + "имя нового файла",
                  path + "файл с запрещенными словами"
          ).get();
          for (var key : resultMap.keySet()) {
              System.out.println(key + " : " + resultMap.get(key));
          }
      }

}