package springbook.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {
    private Calculator calculator;
    private String numFilePath;

    @BeforeEach
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilePath = getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        Integer sum = this.calculator.calcSum(this.numFilePath);
        assertThat(sum).isEqualTo(10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        Integer multiply = this.calculator.calcMultiply(this.numFilePath);
        assertThat(multiply).isEqualTo(24);
    }

    @Test
    public void concatOfNumbers() throws IOException {
        String concat = this.calculator.concat(this.numFilePath);
        assertThat(concat).isEqualTo("1234");
    }
}