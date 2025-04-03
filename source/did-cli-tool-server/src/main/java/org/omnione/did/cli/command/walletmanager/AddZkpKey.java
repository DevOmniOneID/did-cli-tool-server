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

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import org.omnione.did.cli.command.util.CommandUtils;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.key.IWKeyManagerInterface;
import org.omnione.did.wallet.key.WalletManagerFactory;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.util.generator.KeyPairGenerator;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "addZkpKey", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "Wallet add ZKP Key")
public class AddZkpKey implements Callable<Void> {

    @Option(names = {"-m", "--wallet-manager"}, required = true, description =  "Wallet file path")
    public String walletManager;

    @Option(names = {"-p", "--wallet-manager-password"}, required = true, interactive = true, description = "Wallet password")
    public char[] password;

    @Option(names = {"-i", "--key-id"}, required = true, description = "Wallet ZKP key Id")
    public String keyId;

    @Override
    public Void call() throws Exception {
        System.out.println("== wallet add zkp key call ==");
        CommandUtils.printedCommandMessage(this);

        File file = new File(walletManager);
        if (!file.exists()) {
            System.out.println("Not exists WalletManager file...");
            System.out.println("[FAIL] WalletManager add key fail...");
            return null;
        }

        // 1. connect
        WalletManagerInterface manager = WalletManagerFactory.getWalletManager(WalletManagerFactory.WalletManagerType.FILE);
        manager.connect(walletManager, password);

        // 2. add zkp key
        if (manager.isConnect()) {
            CredentialPrimaryPrivateKey privateKey = KeyPairGenerator.generatePrivateKey("masterSecret");

            System.out.println("zkpKeys");
            System.out.println("keyId : " + keyId);
            System.out.println("privateKey");
            System.out.println("p : " + privateKey.getP());
            System.out.println("q : " + privateKey.getQ());

            System.out.println("==========================================");

            ArrayList<String> attrList = new ArrayList<>();
            attrList.add("zkpsex");
            attrList.add("zkpbirth");

            CredentialPrimaryKeyPair credentialPrimaryKeyPair = KeyPairGenerator.generatePublicKey(privateKey.getP(), privateKey.getQ(), attrList, "masterSecret");
            System.out.println(ZkpGsonWrapper.getGson().toJson(credentialPrimaryKeyPair));

            System.out.println("==========================================");
        }

        return null;
    }

}