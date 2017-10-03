package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateListOfAttributes;

public class AttributeValidatorTest {

    private AttributeValidator attributeValidator;

    @Before
    public void setUp() throws Exception {
        attributeValidator = new AttributeValidator();
    }

    @Test
    public void emptyAttributesListTest() {
        List<SingleValidationResult> resultList = attributeValidator.validate(new ArrayList<>(), "1234");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void nullAttributeListTest() {
        List<SingleValidationResult> resultList = attributeValidator.validate(null, "1234");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void oneEmptyAttributeInListTest() {
        List<SingleValidationResult> resultList = attributeValidator.validate(Arrays.asList(new Attribute()), "1234");

        assertFalse(resultList.isEmpty());
        assertNotNull(resultList.get(0).getMessage());
        assertEquals(SingleValidationResultStatus.Error, resultList.get(0).getValidationStatus());
    }

    @Test
    public void listOfAttributesTest() {
        List<SingleValidationResult> resultList = attributeValidator.validate(generateListOfAttributes(), "1234");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void listOfAttributesOneBadAttributeTest() {
        List<Attribute> attributes = generateListOfAttributes();
        attributes.add(new Attribute());
        List<SingleValidationResult> resultList = attributeValidator.validate(attributes, "1234");

        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());

        assertNotNull(resultList.get(0).getMessage());
        assertEquals(SingleValidationResultStatus.Error, resultList.get(0).getValidationStatus());
    }
}
