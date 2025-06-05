import com.kereval.mlang.converter.ConverterException;
import com.kereval.mlang.converter.ParserException;
import net.ihe.gazelle.mlang.converter.JSONMLangConverter;
import net.ihe.gazelle.mlang.converter.MLangJSONConverter;

import java.io.*;

public class MLangConverterTest {

    public static void main(String[] args) throws IOException, ConverterException, ParserException {
        MLangJSONConverter converter = new MLangJSONConverter();
        InputStreamReader is = new InputStreamReader(new FileInputStream("mlang-converter/example.mlang"));
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream("mlang-converter/example.json"));
        converter.convert(is, ow);
        is.close();
        ow.close();

        is = new InputStreamReader(new FileInputStream("mlang-converter/example.json"));
        ow = new OutputStreamWriter(new FileOutputStream("mlang-converter/example_regenerated.mlang"));
        JSONMLangConverter reconverter = new JSONMLangConverter();
        reconverter.convert(is, ow);
        is.close();
        ow.close();
    }
}
