package jp.co.worksap.timachine.spi;

/**
 * Implementation must have and only have a non-arg constructor.
 */
public interface Migration {
    void up();
    void down();
}
