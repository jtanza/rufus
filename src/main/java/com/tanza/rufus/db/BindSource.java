package com.tanza.rufus.db;

import com.tanza.rufus.api.Source;
import org.apache.commons.collections.CollectionUtils;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.*;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

@BindingAnnotation(BindSource.SourceBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindSource {
    class SourceBinderFactory implements BinderFactory {
        @Override
        public Binder build(Annotation annotation) {
            return (Binder<BindSource, Source>) (sql, bindSource, source) -> {
                sql.bind("source", source.getUrl().toString());
                sql.bind("frontpage", source.isFrontpage());

                if (CollectionUtils.isNotEmpty(source.getTags())) {
                    Array array = null;
                    try {
                        array = sql.getContext().getConnection().createArrayOf("text", source.getTags().toArray());
                    } catch (SQLException e) {}
                    sql.bindBySqlType("authors", array, Types.ARRAY);
                }
            };
        }
    }
}
