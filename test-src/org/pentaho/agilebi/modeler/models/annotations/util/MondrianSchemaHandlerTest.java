/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2016 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package org.pentaho.agilebi.modeler.models.annotations.util;

import mondrian.olap.MondrianDef;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.agilebi.modeler.ModelerException;
import org.pentaho.agilebi.modeler.models.annotations.AnnotationUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static junit.framework.Assert.*;

/**
 * Created by pminutillo on 3/5/15.
 */
public class MondrianSchemaHandlerTest {
  private static final String TEST_FILE_PATH = "test-res/products.with.calc.measures.mondrian.xml";
  private static final String TEST_MEASURE_NAME = "TestMeasure";
  private static final String TEST_AGG_TYPE = "SUM";
  private static final String TEST_COLUMN = "ColumnName";


  private static final String TEST_CUBE_NAME = "products_38GA";

  private static final String TEST_CALC_MEMBER_CAPTION = "Updated Test Caption";
  private static final String TEST_CALC_MEMBER_FORMULA = "Updated Test Calc Formula";
  private static final String TEST_CALC_MEMBER_NAME = "Updated Test Calc Name";
  private static final String TEST_CALC_MEMBER_SOURCE_NAME = "Test Calc Name";
  private static final String TEST_CALC_MEMBER_DIMENSION = "Updated Test Calc Dimension";
  private static final String TEST_CALC_MEMBER_DESCRIPTION = "Updated Test Calc Description";
  private static final String TEST_CALC_MEMBER_FORMAT_SCALE = "5";
  private static final String TEST_CALC_MEMBER_FORMAT_CATEGORY = "Currency";

  private static final String TEST_AVERAGE_AGG_TYPE = "avg";
  private static final String TEST_NUM_DECIMAL_FORMAT_STRING = "##.##";
  private static final String TEST_EXISTING_MEASURE_STRING = "bc_BUYPRICE";
  private static final String TEST_CALC_MEMBER_PARENT = "TestParent";
  private static final String TEST_CALC_MEMBER_HIERARCHY = "TestHierarchy";
  private static final String TEST_INVALID_CUBE_NAME = "\"!#$r1R@#!wdfqs  fsd2'fw";
  private static final String TEST_INVALID_EXISTING_MEASURE_STRING = "\"!#$r1R@#!wdfqs  fsd2'fw";
  private static final String TEST_INVALID_CALC_MEMBER_SOURCE_NAME = "\"\\\"!#$r1R@#!wdfqs  fsd2'fw\"";
  private static final String TEST_MISSING_CALC_MEMBER_SOURCE_NAME = "TestCalcMeasure2";

  Document schemaDocument;

  @Before
  public void setUp() throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      schemaDocument = documentBuilder.parse( TEST_FILE_PATH );
    } catch ( ParserConfigurationException e ) {
      e.printStackTrace();
    }

  }

  @Test
  public void testAddMeasureWithoutCubeName() {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AGG_TYPE;
    measure.column = TEST_COLUMN;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler();
    mondrianSchemaHandler.setSchema( schemaDocument );

    Document schema = mondrianSchemaHandler.getSchema();

    try {
      mondrianSchemaHandler.addMeasure( null, measure );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    boolean testMeasureFound = false;
    NodeList nodeList = schema.getElementsByTagName( AnnotationConstants.MEASURE_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      Node measureNode = nodeList.item( x );
      String measureName = measureNode.getAttributes().getNamedItem( MondrianSchemaHandler.MEASURE_NAME_ATTRIBUTE ).getNodeValue();
      if ( measureName != null ) {
        if ( measureName.equals( TEST_MEASURE_NAME ) ) {
          testMeasureFound = true;
        }
      }
    }

    assertTrue( testMeasureFound );
  }

  @Test
  public void testAddMeasure() {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AGG_TYPE;
    measure.column = TEST_COLUMN;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      mondrianSchemaHandler.addMeasure( TEST_CUBE_NAME, measure );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    boolean testMeasureFound = false;
    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.MEASURE_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      Node measureNode = nodeList.item( x );
      String measureName = measureNode.getAttributes().getNamedItem( MondrianSchemaHandler.MEASURE_NAME_ATTRIBUTE ).getNodeValue();
      if ( measureName != null ) {
        if ( measureName.equals( TEST_MEASURE_NAME ) ) {
          testMeasureFound = true;
        }
      }
    }

    assertTrue( testMeasureFound );
  }

  @Test ( expected = org.pentaho.agilebi.modeler.ModelerException.class )
  public void testAddMeasureInvalidCubeName() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AGG_TYPE;
    measure.column = TEST_COLUMN;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    mondrianSchemaHandler.addMeasure( TEST_INVALID_CUBE_NAME, measure );

    boolean testMeasureFound = false;
    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.MEASURE_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      Node measureNode = nodeList.item( x );
      String measureName = measureNode.getAttributes().getNamedItem( MondrianSchemaHandler.MEASURE_NAME_ATTRIBUTE ).getNodeValue();
      if ( measureName != null ) {
        if ( measureName.equals( TEST_MEASURE_NAME ) ) {
          testMeasureFound = true;
        }
      }
    }

  }

  @Test
  public void testAddMeasureWithoutExistingCalculatedMeasures() {
    assertTrue( schemaDocument != null );

    NodeList calculatedMemberNodeList = schemaDocument.getElementsByTagName( AnnotationConstants.CALCULATED_MEMBER_NODE_NAME );
    for ( int x = 0; x <= calculatedMemberNodeList.getLength() - 1; x++ ) {
      Element calculatedMemberNode = (Element) calculatedMemberNodeList.item( x );
      calculatedMemberNode.getParentNode().removeChild( calculatedMemberNode );
    }

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AGG_TYPE;
    measure.column = TEST_COLUMN;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      mondrianSchemaHandler.addMeasure( TEST_CUBE_NAME, measure );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    boolean testMeasureFound = false;
    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.MEASURE_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      Node measureNode = nodeList.item( x );
      String measureName = measureNode.getAttributes().getNamedItem( MondrianSchemaHandler.MEASURE_NAME_ATTRIBUTE ).getNodeValue();
      if ( measureName != null ) {
        if ( measureName.equals( TEST_MEASURE_NAME ) ) {
          testMeasureFound = true;
        }
      }
    }

    assertTrue( testMeasureFound );
  }

  @Test
  public void testAddCalculatedMember() {
    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      mondrianSchemaHandler.addCalculatedMember( null, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    Element measureNode = null;

    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.CALCULATED_MEMBER_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      measureNode = (Element) nodeList.item( x );

      String measureName = measureNode.getAttribute( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE );

      if ( measureName != null ) {
        if ( measureName.equals( TEST_CALC_MEMBER_NAME ) ) {
          break;
        }
      }
    }

    assertNotNull( measureNode );
    assertEquals(
      TEST_CALC_MEMBER_NAME,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_CAPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_FORMULA,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DIMENSION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DESCRIPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );
    NodeList childNodes = measureNode.getChildNodes();
    assertEquals( 3, childNodes.getLength() );
    assertEquals(
      "name1",
      childNodes.item( 1 ).getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_PROPERTY_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals( "value1", childNodes.item( 1 ).getAttributes().getNamedItem( "value" ).getNodeValue() );
    assertEquals( "name2",
      childNodes.item( 2 ).getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_PROPERTY_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals( "value2", childNodes.item( 2 ).getAttributes().getNamedItem( "value" ).getNodeValue() );
  }

  @Test( expected = ModelerException.class )
  public void testAddCalculatedMemberInvalidCubeName() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    mondrianSchemaHandler.addCalculatedMember( TEST_INVALID_CUBE_NAME, calculatedMember );

    Element measureNode = null;

    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.CALCULATED_MEMBER_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      measureNode = (Element) nodeList.item( x );

      String measureName = measureNode.getAttribute( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE );

      if ( measureName != null ) {
        if ( measureName.equals( TEST_CALC_MEMBER_NAME ) ) {
          break;
        }
      }
    }

    assertNotNull( measureNode );
    assertEquals(
        TEST_CALC_MEMBER_NAME,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_CAPTION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_FORMULA,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_DIMENSION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_DESCRIPTION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );
    NodeList childNodes = measureNode.getChildNodes();
    assertEquals( 3, childNodes.getLength() );
    assertEquals(
        "name1",
        childNodes.item( 1 ).getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_PROPERTY_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals( "value1", childNodes.item( 1 ).getAttributes().getNamedItem( "value" ).getNodeValue() );
    assertEquals( "name2",
        childNodes.item( 2 ).getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_PROPERTY_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals( "value2", childNodes.item( 2 ).getAttributes().getNamedItem( "value" ).getNodeValue() );
  }

  @Test
  public void testAddCalculatedMemberInline() {
    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      mondrianSchemaHandler.addCalculatedMember( null, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    Element measureNode = null;

    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.CALCULATED_MEMBER_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      measureNode = (Element) nodeList.item( x );

      String measureName = measureNode.getAttribute( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE );

      if ( measureName != null ) {
        if ( measureName.equals( TEST_CALC_MEMBER_NAME ) ) {
          break;
        }
      }
    }

    assertNotNull( measureNode );
    assertEquals(
        TEST_CALC_MEMBER_NAME,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_CAPTION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_FORMULA,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_DIMENSION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
        TEST_CALC_MEMBER_DESCRIPTION,
        measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );
    NodeList childNodes = measureNode.getChildNodes();
    assertEquals( 3, childNodes.getLength() );

    // Test annotations
    testExpectedAnnotations( measureNode );
  }

  @Test
  public void testUpdateMeasure() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    assertTrue( mondrianSchemaHandler.updateMeasure( null, TEST_EXISTING_MEASURE_STRING, measure ) );

    boolean testMeasureFound = false;
    NodeList nodeList = schemaDocument.getElementsByTagName( AnnotationConstants.MEASURE_NODE_NAME );
    for ( int x = 0; x <= nodeList.getLength() - 1; x++ ) {
      Node measureNode = nodeList.item( x );
      String measureName = measureNode.getAttributes().getNamedItem(
          MondrianSchemaHandler.MEASURE_NAME_ATTRIBUTE ).getNodeValue();
      if ( measureName != null && measureName.equals( TEST_MEASURE_NAME ) ) {
        testMeasureFound = true;
        assertEquals( TEST_AVERAGE_AGG_TYPE,
            measureNode.getAttributes().getNamedItem(
                MondrianSchemaHandler.MEASURE_AGGREGATOR_ATTRIBUTE ).getNodeValue() );
        assertEquals( TEST_NUM_DECIMAL_FORMAT_STRING,
            measureNode.getAttributes().getNamedItem(
                MondrianSchemaHandler.MEASURE_FORMAT_STRING_ATTRIBUTE ).getNodeValue() );
        break;
      }
    }

    assertTrue( testMeasureFound );
  }

  @Test( expected = ModelerException.class )
  public void testUpdateMeasureInvalidMeasureName() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    mondrianSchemaHandler.updateMeasure( null, TEST_INVALID_EXISTING_MEASURE_STRING, measure );
  }


  @Test( expected = ModelerException.class )
  public void testUpdateMeasureBlankMeasureName() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    mondrianSchemaHandler.updateMeasure( null, "", measure );
  }

  @Test( expected = ModelerException.class )
  public void testUpdateMeasureInvalidCubeName() throws ModelerException {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    mondrianSchemaHandler.updateMeasure( TEST_INVALID_CUBE_NAME, TEST_EXISTING_MEASURE_STRING, measure );
  }

  @Test
  public void testUpdateCalculatedMember() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    assertTrue( result );

    Element measureNode = AnnotationUtil.getCalculatedMemberNode( schemaDocument, TEST_CUBE_NAME, TEST_CALC_MEMBER_NAME );

    assertEquals(
      TEST_CALC_MEMBER_NAME,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_CAPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_FORMULA,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DIMENSION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DESCRIPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );

    // Test annotations
    testExpectedAnnotations( measureNode );
  }

  @Test
  public void testUpdateCalculatedMemberMissingProperties() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();
    calculatedMember.name = "";
    calculatedMember.caption = "";
    calculatedMember.description = "";
    calculatedMember.formula = "";
    calculatedMember.dimension = "";

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    assertTrue( result );

    Element measureNode = AnnotationUtil.getCalculatedMemberNode( schemaDocument, TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME );

    // Test annotations
    testExpectedAnnotations( measureNode );
  }

  @Test( expected = ModelerException.class )
  public void testUpdateCalculatedMemberInvalidCalculatedMemberSourceName() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_INVALID_CALC_MEMBER_SOURCE_NAME, calculatedMember );
  }

  @Test( expected = ModelerException.class )
  public void testUpdateCalculatedMemberBlankCalculatedMemberSourceName() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, "", calculatedMember );
  }

  @Test
  public void testUpdateCalculatedMemberMissingCalculatedMemberSource() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_MISSING_CALC_MEMBER_SOURCE_NAME, calculatedMember );

    assertFalse( result );
  }

  @Test
  public void testUpdateCalculatedMemberWithoutAnnotations() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    calculatedMember.annotations.array = new MondrianDef.Annotation[0];

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    assertTrue( result );

    Element measureNode = AnnotationUtil.getCalculatedMemberNode( schemaDocument, TEST_CUBE_NAME, TEST_CALC_MEMBER_NAME );

    assertEquals(
      TEST_CALC_MEMBER_NAME,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_CAPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_FORMULA,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DIMENSION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DESCRIPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );

  }

  @Test
  public void testUpdateCalculatedMemberWithoutExistingAnnotations() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    NodeList annotationsNodes = schemaDocument.getElementsByTagName( AnnotationConstants.ANNOTATIONS_NODE_NAME );
    annotationsNodes.item( 0 ).getParentNode().removeChild( annotationsNodes.item( 0 ) );

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    assertTrue( result );

    Element measureNode = AnnotationUtil.getCalculatedMemberNode( schemaDocument, TEST_CUBE_NAME, TEST_CALC_MEMBER_NAME );

    assertEquals(
      TEST_CALC_MEMBER_NAME,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_CAPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_FORMULA,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DIMENSION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DESCRIPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );

  }

  @Test
  public void testUpdateCalculatedMemberWithSomeAnnotations() throws ModelerException {
    boolean result = true;

    assertTrue( schemaDocument != null );

    MondrianDef.CalculatedMember calculatedMember = getMockCalculatedMember();

    // remove some existing annotations
    NodeList annotationsNodes = schemaDocument.getElementsByTagName( AnnotationConstants.ANNOTATIONS_NODE_NAME );
    Element annotationsNode = (Element) annotationsNodes.item( 0 );
    NodeList annotationNodes = annotationsNode.getElementsByTagName( AnnotationConstants.ANNOTATION_NODE_NAME );
    for ( int x = 0; x <= annotationNodes.getLength() - 3; x++ ) {
      annotationsNode.removeChild( annotationNodes.item( x ) );
    }

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    try {
      result = mondrianSchemaHandler.updateCalculatedMember( TEST_CUBE_NAME, TEST_CALC_MEMBER_SOURCE_NAME, calculatedMember );
    } catch ( ModelerException e ) {
      e.printStackTrace();
    }

    assertTrue( result );

    Element measureNode = AnnotationUtil.getCalculatedMemberNode( schemaDocument, TEST_CUBE_NAME, TEST_CALC_MEMBER_NAME );

    assertEquals(
      TEST_CALC_MEMBER_NAME,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_CAPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_CAPTION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_FORMULA,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_FORMULA_ATTRIBUTE ) .getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DIMENSION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DIMENSION_ATTRIBUTE ).getNodeValue() );
    assertEquals(
      TEST_CALC_MEMBER_DESCRIPTION,
      measureNode.getAttributes().getNamedItem( AnnotationConstants.CALCULATED_MEMBER_DESCRIPTION_ATTRIBUTE ).getNodeValue() );

    // check for mondrian annotations added by the updateCalculatedMember call like formatScale
    annotationNodes = schemaDocument.getElementsByTagName( AnnotationConstants.ANNOTATION_NODE_NAME );
    boolean missingAnnotationFound = Boolean.FALSE;
    for ( int x = 0; x <= annotationNodes.getLength() - 1; x++ ) {
      if ( annotationNodes.item( x )
        .getAttributes()
        .getNamedItem( AnnotationConstants.CALCULATED_MEMBER_NAME_ATTRIBUTE )
        .getTextContent()
        .equals( AnnotationConstants.CALCULATED_MEMBER_FORMAT_SCALE ) ) {
        missingAnnotationFound = Boolean.TRUE;
      }
    }

    assertTrue( missingAnnotationFound );
  }

  @Test
  public void testUpdatingMeasureNotFoundReturnsFalse() throws Exception {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_MEASURE_NAME;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    assertFalse( mondrianSchemaHandler.updateMeasure( null, "MeasureNotFound", measure ) );
  }

  @Test
  public void testUpdatingMeasureToExistingReturnsFalse() throws Exception {
    assertTrue( schemaDocument != null );

    MondrianDef.Measure measure = new MondrianDef.Measure();
    measure.name = TEST_EXISTING_MEASURE_STRING;
    measure.aggregator = TEST_AVERAGE_AGG_TYPE;
    measure.formatString = TEST_NUM_DECIMAL_FORMAT_STRING;

    MondrianSchemaHandler mondrianSchemaHandler = new MondrianSchemaHandler( schemaDocument );

    assertFalse( mondrianSchemaHandler.updateMeasure( null, "bc_QUANTITYINSTOCK", measure ) );
  }

  /**
   * Build mock calc member objects for testing
   *
   * @return
   */
  private MondrianDef.CalculatedMember getMockCalculatedMember() {
    MondrianDef.CalculatedMember calculatedMember = new MondrianDef.CalculatedMember();
    calculatedMember.caption = TEST_CALC_MEMBER_CAPTION;
    calculatedMember.formula = TEST_CALC_MEMBER_FORMULA;
    calculatedMember.name = TEST_CALC_MEMBER_NAME;
    calculatedMember.dimension = TEST_CALC_MEMBER_DIMENSION;
    calculatedMember.description = TEST_CALC_MEMBER_DESCRIPTION;
    calculatedMember.visible = true;
    calculatedMember.hierarchy = TEST_CALC_MEMBER_HIERARCHY;
    calculatedMember.parent = TEST_CALC_MEMBER_PARENT;
    MondrianDef.CalculatedMemberProperty property1 = new MondrianDef.CalculatedMemberProperty();
    property1.name = "name1";
    property1.value = "value1";
    MondrianDef.CalculatedMemberProperty property2 = new MondrianDef.CalculatedMemberProperty();
    property2.name = "name2";
    property2.value = "value2";
    calculatedMember.memberProperties = new MondrianDef.CalculatedMemberProperty[]{property1, property2};

    MondrianDef.Annotation formatScaleAnnotation = new MondrianDef.Annotation();
    formatScaleAnnotation.name = AnnotationConstants.CALCULATED_MEMBER_FORMAT_SCALE;
    formatScaleAnnotation.cdata = TEST_CALC_MEMBER_FORMAT_SCALE;

    MondrianDef.Annotation formatCategoryAnnotation = new MondrianDef.Annotation();
    formatCategoryAnnotation.name = AnnotationConstants.CALCULATED_MEMBER_FORMAT_CATEGORY;
    formatCategoryAnnotation.cdata = TEST_CALC_MEMBER_FORMAT_CATEGORY;

    MondrianDef.Annotation inlineAnnotation = new MondrianDef.Annotation();
    inlineAnnotation.name = AnnotationConstants.CALCULATED_MEMBER_INLINE;
    inlineAnnotation.cdata = "false";

    MondrianDef.Annotation formulaExpressionAnnotation = new MondrianDef.Annotation();
    formulaExpressionAnnotation.name = AnnotationConstants.CALCULATED_MEMBER_FORMULA_EXPRESSION;
    formulaExpressionAnnotation.cdata = TEST_CALC_MEMBER_FORMULA;

    MondrianDef.Annotation calcSubtotalsAnnotation = new MondrianDef.Annotation();
    calcSubtotalsAnnotation.name = AnnotationConstants.CALCULATED_MEMBER_CALC_SUBTOTALS;
    calcSubtotalsAnnotation.cdata = "false";

    MondrianDef.Annotations annotations = new MondrianDef.Annotations();
    annotations.array = new MondrianDef.Annotation[] {
      formatScaleAnnotation,
      formatCategoryAnnotation,
      inlineAnnotation,
      formulaExpressionAnnotation,
      calcSubtotalsAnnotation
    };

    calculatedMember.annotations = annotations;

    return calculatedMember;
  }

  /**
   * Test for updated annotation values
   *
   * @param measureNode
   */
  private void testExpectedAnnotations( Element measureNode ) {
    // Test annotations
    NodeList annotationsNodes = measureNode.getElementsByTagName( AnnotationConstants.ANNOTATIONS_NODE_NAME );
    if ( annotationsNodes.getLength() <= 0 ) {
      fail( AnnotationConstants.NO_ANNOTATIONS_FOUND_MESSAGE );
    }

    // assume the first element is the only annotations node, as per the spec
    Element annotationsNode = (Element) annotationsNodes.item( 0 );
    NodeList annotationNodes = annotationsNode.getElementsByTagName( AnnotationConstants.ANNOTATION_NODE_NAME );
    for ( int i = 0; i <= annotationNodes.getLength() - 1; i++ ) {
      Node annotationNode = annotationNodes.item( i );
      switch ( annotationNode.getAttributes().getNamedItem( "name" ).getTextContent() ) {
        case AnnotationConstants.CALCULATED_MEMBER_FORMAT_CATEGORY :
          assertTrue( annotationNode.getTextContent().equals( TEST_CALC_MEMBER_FORMAT_CATEGORY ) );
          break;
        case AnnotationConstants.CALCULATED_MEMBER_FORMAT_SCALE :
          assertTrue( annotationNode.getTextContent().equals( TEST_CALC_MEMBER_FORMAT_SCALE ) );
          break;
        case AnnotationConstants.CALCULATED_MEMBER_FORMULA_EXPRESSION :
          assertTrue( annotationNode.getTextContent().equals( TEST_CALC_MEMBER_FORMULA ) );
          break;
        case AnnotationConstants.CALCULATED_MEMBER_INLINE :
          annotationNode.setNodeValue( "false" );
          break;
        case AnnotationConstants.CALCULATED_MEMBER_CALC_SUBTOTALS :
          annotationNode.setNodeValue( "false" );
          break;
      }
    }
  }
}
