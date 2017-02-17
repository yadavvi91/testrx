package org.yadavvi.testrx;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.yadavvi.testrx.RxJavaExample.WORDS;

/**
 * Created by vishal on 17/2/17.
 */
public class RxJavaExampleTest {

    @Test
    public void testInSameThread() {
        // given
        final List<String> result = new ArrayList<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribe(new Consumer<String>() {
            public void accept(@NonNull String s) throws Exception {
                result.add(s);
            }
        });

        // then
        assertThat(result, notNullValue());
        assertThat(result, hasSize(9));
        assertThat(result, hasItem(" 4. fox"));
    }
}