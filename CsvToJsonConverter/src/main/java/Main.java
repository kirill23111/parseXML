
    import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
    import org.w3c.dom.Document;
    import org.w3c.dom.Node;
    import org.w3c.dom.NodeList;
    import org.w3c.dom.Element;
    import javax.xml.parsers.DocumentBuilder;
    import javax.xml.parsers.DocumentBuilderFactory;
    import java.io.*;
import java.lang.reflect.Type;
    import java.util.ArrayList;
    import java.util.List;

    public class Main {
        public static void main(String[] args) {
            String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
            String fileName = "data.csv";
            List<Employee> list = parseCSV(columnMapping, fileName);

            String json = listToJson(list);
            writeString(json, "data.json");

            List<Employee> xmlList = parseXML("data.xml");
            String xmlJson = listToJson(xmlList);
            writeString(xmlJson, "data2.json");

        }

        public static List<Employee> parseXML(String fileName) {
            List<Employee> employees = new ArrayList<>();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new File(fileName));

                Node root = doc.getDocumentElement();  // <staff>
                NodeList nodeList = root.getChildNodes();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                        String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                        String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                        String country = element.getElementsByTagName("country").item(0).getTextContent();
                        int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                        employees.add(new Employee(id, firstName, lastName, country, age));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return employees;
        }
        
        public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
            try (Reader reader = new FileReader(fileName)) {
                ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Employee.class);
                strategy.setColumnMapping(columnMapping);

                CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                        .withMappingStrategy(strategy)
                        .build();

                return csvToBean.parse();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String listToJson(List<Employee> list) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            Type listType = new TypeToken<List<Employee>>() {}.getType();
            return gson.toJson(list, listType);
        }

        public static void writeString(String json, String fileName) {
            try (FileWriter file = new FileWriter(fileName)) {
                file.write(json);
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
