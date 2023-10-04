package com.example.modules;

import com.example.*;
import com.example.ptf.ListTableFunction;
import com.example.ptf.ReadFileTableFunction;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import io.airlift.http.client.HttpClientConfig;
import io.opentelemetry.api.OpenTelemetry;
import io.trino.spi.function.table.ConnectorTableFunction;
import io.trino.spi.type.TypeManager;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.http.client.HttpClientBinder.httpClientBinder;
import static java.util.Objects.requireNonNull;

public class LLmModule implements Module {

    private final TypeManager typeManager;

    public LLmModule(TypeManager typeManager) {
        this.typeManager = requireNonNull(typeManager, "typeManager is null");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeManager.class).toInstance(typeManager);
        binder.bind(LLmConnector.class).in(Scopes.SINGLETON);
        binder.bind(LLmMetadata.class).in(Scopes.SINGLETON);
        binder.bind(LLmClient.class).in(Scopes.SINGLETON);
        binder.bind(LLmSplitManager.class).in(Scopes.SINGLETON);
        binder.bind(LLmRecordSetProvider.class).in(Scopes.SINGLETON);
        newSetBinder(binder, ConnectorTableFunction.class).addBinding().toProvider(ReadFileTableFunction.class).in(Scopes.SINGLETON);
        newSetBinder(binder, ConnectorTableFunction.class).addBinding().toProvider(ListTableFunction.class).in(Scopes.SINGLETON);
        binder.bind(OpenTelemetry.class).toInstance(OpenTelemetry.noop());
        configBinder(binder).bindConfig(HttpClientConfig.class, ForLLm.class);
        httpClientBinder(binder).bindHttpClient("llm", ForLLm.class);
    }
}
