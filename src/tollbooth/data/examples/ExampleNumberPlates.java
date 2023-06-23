package tollbooth.data.examples;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Used for instatiating a random license plate (BH-based)
 */
public class ExampleNumberPlates {
    public static List<String> NUMBER_PLATES = Arrays.asList(
            "Z90-Y-L35", "S73-I-X59", "B40-B-Y86",
            "X89-E-L98", "Q65-O-G44", "G92-D-T29",
            "U76-N-H09", "Y92-D-Q92", "P09-L-W39",
            "Y12-K-G54", "R74-S-J69", "W26-G-I51",
            "A73-U-R88", "B37-L-F09", "G06-T-N07",
            "I80-V-S59", "B50-N-E74", "P01-Y-K17",
            "L88-F-O76", "T36-G-F16", "Y20-S-B99",
            "T95-W-X35", "F14-M-L43", "E11-O-Z19",
            "U30-N-E86", "O99-M-P68", "J65-D-O03",
            "N19-S-W18", "T40-X-J60", "D84-K-T46",
            "Z52-Q-I10", "R85-Q-M08", "O42-H-Q23",
            "R71-R-W25", "G65-G-T26", "M12-R-Q77",
            "C57-U-K41", "W99-X-T87", "E40-W-T92",
            "A46-X-G23", "X23-T-H00", "I75-U-B14",
            "W38-L-U56", "Q86-W-T62", "M48-Z-F45",
            "B78-S-E82", "E64-A-X14", "H24-T-Q17",
            "K89-L-F58", "J45-Q-Q47", "F45-O-F72",
            "L76-B-V37", "K79-A-F32", "T89-K-F67",
            "V80-H-O83", "T73-V-Q09", "K89-R-R59",
            "I20-L-B39", "A45-M-R21", "Z70-H-P50",
            "Q34-S-B07", "S64-S-B46", "J08-J-R18",
            "Q13-N-O17", "J34-P-P81", "G40-P-F62",
            "K08-E-O26", "C06-F-C26", "H52-I-J85",
            "V54-J-K94", "D87-L-B70", "W46-P-Y05",
            "A03-R-F45", "O41-G-G94", "K18-F-M04",
            "L97-E-A91", "G44-S-J11", "C10-V-W01",
            "L34-R-Q69", "K94-V-J22");

    /**
     * Fetches a random license plate number, using the Random() generator
     * @return A randomly chosen license plate number.
     */
    public static String getRandom() {
        Random rng = new Random();
        Integer idx = rng.nextInt(ExampleNumberPlates.NUMBER_PLATES.size() - 1);
        String fetched = ExampleNumberPlates.NUMBER_PLATES.get(idx);
        ExampleNumberPlates.NUMBER_PLATES.remove(idx);

        return fetched;
    }
}
