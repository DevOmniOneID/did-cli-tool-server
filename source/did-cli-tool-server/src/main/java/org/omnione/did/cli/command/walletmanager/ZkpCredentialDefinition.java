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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import org.omnione.did.cli.command.util.CommandUtils;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.key.IWKeyManagerInterface;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialDefinitionPrivateKey;
import org.omnione.did.wallet.zkp.util.generator.KeyCorrectnessProofGenerator;
import org.omnione.did.wallet.zkp.util.generator.KeyPairGenerator;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;
import org.omnione.did.wallet.zkp.util.helper.CredentialDefinitionHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "createZkpCredDef", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "Create ZKP AnonCreds CredentialDefinition")
public class ZkpCredentialDefinition implements Callable<Void> {

    @Option(names = {"-s", "--zkp-credential-schema"}, required = true, description = "ZKP credential schema file path")
    public String zkpCredSchema;

    @Option(names = {"-m", "--wallet-manager"}, required = true, description = "Wallet file path")
    public String walletManager;

    @Option(names = {"-p", "--wallet-manager-password"}, required = true, interactive = true, description = "Wallet password")
    public char[] password;

    @Override
    public Void call() throws Exception {
        System.out.println("== new zkp credential definition create call ==");
        CommandUtils.printedCommandMessage(this);

        // 0. load zkp credential schema
        BufferedReader reader = new BufferedReader(new FileReader(zkpCredSchema));
        StringBuilder credSchemaStr = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            credSchemaStr.append(str);
        }
        CredentialSchema credSchema = ZkpGsonWrapper.getGson().fromJson(credSchemaStr.toString(), CredentialSchema.class);
        System.out.println("credSchema : " + credSchema);

        // 1. create zkp credential definition
        CredentialPrimaryKeyPair credentialPrimaryKeyPair = KeyPairGenerator.generateKeyPair(credSchema.getAttrNames(), "masterSecret");
        KeyCorrectnessProof keyCorrectnessProof = KeyCorrectnessProofGenerator.generateKeyProof(credentialPrimaryKeyPair);
        CredentialDefinition credentialDefinition = CredentialDefinitionHelper.generateCredentialDefinition(credSchema.getId().split(":")[0], credSchema.getId(), credSchema.getVersion(), credSchema.getTag(), credentialPrimaryKeyPair.getPublicKey(), null);
        CredentialDefinitionInfo credDefInfo = new CredentialDefinitionInfo(credentialDefinition, new CredentialDefinitionPrivateKey(credentialPrimaryKeyPair.getPrivateKey(), null), keyCorrectnessProof);

        //2. insert credential definition to zkp wallet
        IWKeyManager keyManager = new IWKeyManager(walletManager);
        Sha256 hash = Sha256.from(String.valueOf(password).getBytes());
        byte[] key = hash.getBytes();

        if(!keyManager.isUnLock()) {
            keyManager.unLock(key, new IWKeyManagerInterface.OnUnLockListener() {
                @Override
                public void onSuccess() {
                    try {
                        keyManager.addCredDefInfos(ZkpGsonWrapper.getGson().toJson(credDefInfo));

                        System.out.println("[SUCCESS] ZKP CredentialDefinition generate success...");
                    } catch(Exception e) {
                        System.out.println("[FAIL] ZKP CredentialDefinition fail...");
                    }
                }

                @Override
                public void onFail(int errCode) {
                    System.out.println("[FAIL] ZKP CredentialDefinition fail...");
                }

                @Override
                public void onCancel() {
                    System.out.println("[FAIL] ZKP CredentialDefinition fail...");
                }
            });
        }

        return null;
    }

}