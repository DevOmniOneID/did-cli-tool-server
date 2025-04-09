/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.cli.command.walletmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import org.omnione.did.cli.command.util.CommandUtils;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.key.IWKeyManagerInterface;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.revoc.sdk.response.ZkpCreateSchemaResponse;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "createZkpCredSchema", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "Create ZKP AnonCreds CredentialSchema")
public class ZkpCredentialSchema implements Callable<Void> {

    @Option(names = {"-s", "--zkp-credential-schema"}, required = true, description = "ZKP credential schema file path")
    public String zkpCredSchema;

    @Option(names = {"-sn", "--zkp-schema-name"}, required = true, description = "ZKP schema name")
    public String zkpSchemaName;

    @Option(names = {"-id", "--issuer-did"}, required = true, description = "Issuer DID")
    public String issuerDid;

    @Option(names = {"-sv", "--zkp-schema-version"}, required = true, description = "ZKP schema version")
    public String zkpSchemaVersion;

    @Option(names = {"-st", "--zkp-schema-tag"}, required = true, description = "ZKP schema tag")
    public String zkpSchemaTag;

    @Option(names = {"-al", "--zkp-attribute-list"}, required = true, split = ",", description = "ZKP attribute list")
    public List<String> attributeList;

    @Option(names = {"-r", "--file-remove"}, description = "if zkp credential schema file exists, delete it and re-generate")
    public boolean existFileRemove = false;

    @Override
    public Void call() throws Exception {
        System.out.println("== new zkp credential schema create call ==");
        CommandUtils.printedCommandMessage(this);

        // 0. remove file
        if (existFileRemove) {
            File file = new File(zkpCredSchema);
            if (file.exists()) {
                System.out.println("[SUCCESS] ZKP credential schema file remove : " + zkpCredSchema);
                file.delete();
            }
        }

        // 1. create zkp credentail schema
        ZkpCreateSchemaResponse schemaResponse;

        CredentialSchema credentialSchema = new CredentialSchema();
        credentialSchema.setId(issuerDid + ":" + "2" + ":" + zkpSchemaName + ":" + zkpSchemaVersion);
        credentialSchema.setVersion(zkpSchemaVersion);
        credentialSchema.setName(zkpSchemaName);
        credentialSchema.setAttrNames(attributeList);
        credentialSchema.setTag(zkpSchemaTag);

        File file = new File(zkpCredSchema);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(credentialSchema.toJson());
        writer.close();

        return null;
    }

}