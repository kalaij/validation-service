package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateListOfAttributes;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AttributeValidatorTest {

    private AttributeValidator attributeValidator;

    @Before
    public void setUp() throws Exception {
        attributeValidator = new AttributeValidator();
    }

    @Test
    public void emptyAttributesListTest() {
        List<SingleValidationResult> resultList =
                attributeValidator.validate("", new ArrayList<>(), "1234");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void nullAttributeListTest() {
        List<SingleValidationResult> resultList =
                attributeValidator.validate("", null, "1234");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void oneEmptyAttributeInListTest() {
        List<SingleValidationResult> resultList =
                attributeValidator.validate("attributeName", Arrays.asList(new Attribute()), "1234");

        assertFalse(resultList.isEmpty());
        assertNotNull(resultList.get(0).getMessage());
        assertEquals(SingleValidationResultStatus.Error, resultList.get(0).getValidationStatus());
    }

    @Test
    public void listOfAttributesTest() {
        List<SingleValidationResult> resultList =
                ValidatorHelper.validateAttribute(generateListOfAttributes(), "1234", attributeValidator);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void listOfAttributesOneBadAttributeTest() {
        Map<String, Collection<Attribute>> attributes = generateListOfAttributes();
        attributes.put(null, Collections.singletonList(new Attribute()));
        List<SingleValidationResult> resultList =
                ValidatorHelper.validateAttribute(attributes, "1234", attributeValidator);

        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size());

        assertNotNull(resultList.get(0).getMessage());
        assertEquals(SingleValidationResultStatus.Error, resultList.get(0).getValidationStatus());
        assertNotNull(resultList.get(1).getMessage());
        assertEquals(SingleValidationResultStatus.Error, resultList.get(1).getValidationStatus());
    }
}
