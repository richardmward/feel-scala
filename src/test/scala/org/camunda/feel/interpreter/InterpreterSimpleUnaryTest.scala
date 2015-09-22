package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * 10.3.2.8 Decision Table
 * The normative notation for decision tables is specified in clause 8. A textual representation using invocation of the decision
 * table built-in function is provided here in order to tie the syntax to the semantics in the same way as is done for the rest of
 * FEEL. Unary tests (grammar rule 17) cannot be mapped to the semantic domain in isolation, and are left as their syntactic
 * forms, indicated by the enclosing single-quotes. For example, the first decision table in Table 26 can be represented
 * textually as
 * decision table(
 * outputs: "Applicant Risk Rating",
 * input expression list: [Applicant Age, Medical History],
 * rule list: [
 * ['>60', '"good"', '"Medium"'],
 * ['>60', '"bad"', '"High"'],
 * ['[25..60]', '-', '"Medium"'],
 * ['<25', '"good"','"Low"'],
 * ['<25', '"bad"', '"Medium"']],
 * hit policy: "Unique")
 * The decision table built-in in clause 10.3.4.6 will compose the unary tests syntax into expressions that can be mapped to the
 * FEEL semantic domain.
 *
 * @author Philipp Ossler
 */
class InterpreterSimpleUnaryTest extends FlatSpec with Matchers {

  val parser = new FeelParser
  val interpreter = new FeelInterpreter

  "A number" should "compare with '<'" in {

    eval(2, "< 3") should be(ValBoolean(true))
    eval(3, "< 3") should be(ValBoolean(false))
    eval(4, "< 3") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(2, "<= 3") should be(ValBoolean(true))
    eval(3, "<= 3") should be(ValBoolean(true))
    eval(4, "<= 3") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(2, "> 3") should be(ValBoolean(false))
    eval(3, "> 3") should be(ValBoolean(false))
    eval(4, "> 3") should be(ValBoolean(true))
  }
  
  it should "compare with '>='" in {

    eval(2, ">= 3") should be(ValBoolean(false))
    eval(3, ">= 3") should be(ValBoolean(true))
    eval(4, ">= 3") should be(ValBoolean(true))
  }

  it should "compare with another number (implicit '=')" in {
    
    eval(2, "3") should be (ValBoolean(false))
    eval(3, "3") should be (ValBoolean(true))
  }
  
  it should "be in interval '(2 .. 4)'" in {

    eval(2, "(2 .. 4)") should be(ValBoolean(false))
    eval(3, "(2 .. 4)") should be(ValBoolean(true))
    eval(4, "(2 .. 4)") should be(ValBoolean(false))
  }

  it should "be in interval '[2 .. 4]'" in {

    eval(2, "[2 .. 4]") should be(ValBoolean(true))
    eval(3, "[2 .. 4]") should be(ValBoolean(true))
    eval(4, "[2 .. 4]") should be(ValBoolean(true))
  }
  
  it should "be in '2,3'" in {
    
    eval(2, "2,3") should be (ValBoolean(true))
    eval(3, "2,3") should be (ValBoolean(true))
    eval(4, "2,3") should be (ValBoolean(false))
  }
  
  it should "be not equal 'not(3)'" in {
    
    eval(2, "not(3)") should be (ValBoolean(true))
    eval(3, "not(3)") should be (ValBoolean(false))
    eval(4, "not(3)") should be (ValBoolean(true))
  }
  
  it should "be not in 'not(2,3)'" in {
    
    eval(2, "not(2,3)") should be (ValBoolean(false))
    eval(3, "not(2,3)") should be (ValBoolean(false))
    eval(4, "not(2,3)") should be (ValBoolean(true))
  }
  
  "A string" should "compare to another string (implicit '=')" in {
    
    eval("a", """ "b" """) should be (ValBoolean(false))
    eval("b", """ "b" """) should be (ValBoolean(true))
  }
  
  it should """be in '"a","b"' """ in {
    
    eval("a", """ "a","b" """) should be (ValBoolean(true))
    eval("b", """ "a","b" """) should be (ValBoolean(true))
    eval("c", """ "a","b" """) should be (ValBoolean(false))
  }
  
  "A boolean" should "compare to another string (implicit '=')" in {
    
    eval(false, "true") should be (ValBoolean(false))
    eval(true, "false") should be (ValBoolean(false))
    
    eval(false, "false") should be (ValBoolean(true))
    eval(true, "true") should be (ValBoolean(true))
  }

  "A date" should "compare with '<'" in {

    eval(asDate("2015-09-17"), """< date("2015-09-18")""") should be(ValBoolean(true))
    eval(asDate("2015-09-18"), """< date("2015-09-18")""") should be(ValBoolean(false))
    eval(asDate("2015-09-19"), """< date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(asDate("2015-09-17"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(asDate("2015-09-18"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(asDate("2015-09-19"), """<= date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(asDate("2015-09-17"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(asDate("2015-09-18"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(asDate("2015-09-19"), """> date("2015-09-18")""") should be(ValBoolean(true))
  }
  
  it should "compare with '>='" in {

    eval(asDate("2015-09-17"), """>= date("2015-09-18")""") should be(ValBoolean(false))
    eval(asDate("2015-09-18"), """>= date("2015-09-18")""") should be(ValBoolean(true))
    eval(asDate("2015-09-19"), """>= date("2015-09-18")""") should be(ValBoolean(true))
  }
  
  it should "compare with another date (implicit '=')" in {
    
    eval(asDate("2015-09-17"), """date("2015-09-18")""") should be (ValBoolean(false))
    eval(asDate("2015-09-18"), """date("2015-09-18")""") should be (ValBoolean(true))
  }

  it should """be in interval '(date("2015-09-17") .. date("2015-09-19")]'""" in {

    eval(asDate("2015-09-17"), """(date("2015-09-17") .. date("2015-09-19"))""") should be(ValBoolean(false))
    eval(asDate("2015-09-18"), """(date("2015-09-17") .. date("2015-09-19"))""") should be(ValBoolean(true))
    eval(asDate("2015-09-19"), """(date("2015-09-17") .. date("2015-09-19"))""") should be(ValBoolean(false))
  }

  it should """be in interval '[date("2015-09-17") .. date("2015-09-19")]'""" in {

    eval(asDate("2015-09-17"), """[date("2015-09-17") .. date("2015-09-19")]""") should be(ValBoolean(true))
    eval(asDate("2015-09-18"), """[date("2015-09-17") .. date("2015-09-19")]""") should be(ValBoolean(true))
    eval(asDate("2015-09-19"), """[date("2015-09-17") .. date("2015-09-19")]""") should be(ValBoolean(true))
  }
  
  "An empty expression ('-')" should "be always true" in {
    
    eval(None, "-") should be (ValBoolean(true))
  }
  
  "An empty expression ('')" should "be always true" in {
    
    eval(None, "") should be (ValBoolean(true))
  }

  private def eval(input: Any, expression: String): Val = {
    val exp = parser.parseSimpleUnaryTest(expression)
    interpreter.value(exp.get)(Context(input))
  }

  private def asDate(date: String): Date = date

}