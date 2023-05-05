package com.weiservers.scnet;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.zip.DataFormatException;

import static com.weiservers.scnet.utils.DataConvertUtils.decompress;
import static com.weiservers.scnet.utils.DataConvertUtils.hexStringToByteArray;

public class JieYaTest {
    @Test
    void JieYaTest01() throws DataFormatException, IOException {

        System.out.println(decompress(hexStringToByteArray("ebe06564600000")));
    }
}
