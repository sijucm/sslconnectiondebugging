package com.siju.tools.httpscall.util;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.ChainedTokenCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import io.netty.resolver.DefaultAddressResolverGroup;
import java.util.logging.Level;

public class KeyVaultUtil {
  public String getSecret(String keyVaultUrl, String secretName) {

    final ManagedIdentityCredentialBuilder managedIdentityCredentialBuilder =
        new ManagedIdentityCredentialBuilder();
    final AzureCliCredentialBuilder azureCliCredentialBuilder = new AzureCliCredentialBuilder();

    final ChainedTokenCredentialBuilder chainedTokenCredentialBuilder =
        new ChainedTokenCredentialBuilder()
            .addLast(managedIdentityCredentialBuilder.build())
            .addLast(azureCliCredentialBuilder.build());

    reactor.netty.http.client.HttpClient nettyHttpClient =
        reactor.netty.http.client.HttpClient.create()
            .resolver(DefaultAddressResolverGroup.INSTANCE);
    HttpClient httpClient = new NettyAsyncHttpClientBuilder(nettyHttpClient).build();

    SecretClient secretClient =
        new SecretClientBuilder()
            .httpClient(httpClient)
            .vaultUrl(keyVaultUrl)
            // .credential(managedIdentityCredentialBuilder.build()).buildClient();
            .credential(chainedTokenCredentialBuilder.build())
            .buildClient();

    KeyVaultSecret keyVaultSecret = secretClient.getSecret(secretName);


    return keyVaultSecret.getValue();
  }
}
