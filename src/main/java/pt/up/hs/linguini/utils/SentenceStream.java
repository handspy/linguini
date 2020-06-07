package pt.up.hs.linguini.utils;

import java.nio.CharBuffer;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream of text sentences.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SentenceStream extends Spliterators.AbstractSpliterator<String>
        implements Consumer<CharSequence> {

    private final Spliterator<? extends CharSequence> source;
    private CharBuffer buffer;
    private final BreakIterator iterator;

    public SentenceStream(Spliterator<? extends CharSequence> source) {
        this(Locale.ENGLISH, source);
    }

    public SentenceStream(
            Locale locale,
            Spliterator<? extends CharSequence> source) {
        super(Long.MAX_VALUE, ORDERED | NONNULL);
        this.source = source;
        iterator = BreakIterator.getSentenceInstance(locale);
        buffer = CharBuffer.allocate(100);
        buffer.flip();
    }

    public static Stream<String> sentences(Stream<? extends CharSequence> s) {
        return StreamSupport.stream(new SentenceStream(s.spliterator()), false);
    }

    public static Stream<String> sentences(
            Locale locale,
            Stream<? extends CharSequence> s) {
        return StreamSupport.stream(
                new SentenceStream(locale, s.spliterator()), false);
    }

    @Override
    public boolean tryAdvance(Consumer<? super String> action) {
        for (; ; ) {
            int next = iterator.next();
            if (next != BreakIterator.DONE && next != buffer.limit()) {
                action.accept(buffer.subSequence(0, next - buffer.position()).toString());
                buffer.position(next);
                return true;
            }
            if (!source.tryAdvance(this)) {
                if (buffer.hasRemaining()) {
                    action.accept(buffer.toString());
                    buffer.position(0).limit(0);
                    return true;
                }
                return false;
            }
            iterator.setText(buffer.toString());
        }
    }

    @Override
    public void accept(CharSequence t) {
        buffer.compact();
        if (buffer.remaining() < t.length()) {
            CharBuffer bigger = CharBuffer.allocate(
                    Math.max(buffer.capacity() * 2, buffer.position() + t.length()));
            buffer.flip();
            bigger.put(buffer);
            buffer = bigger;
        }
        buffer.append(t).flip();
    }
}
