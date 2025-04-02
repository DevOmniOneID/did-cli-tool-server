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
import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import org.omnione.did.cli.command.util.CommandUtils;
import org.omnione.did.wallet.enums.WalletEncryptType;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.key.IWKeyManagerInterface;
import org.omnione.did.wallet.key.WalletManagerFactory;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.wallet.key.data.IWKey;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "createZkpWallet", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "Create ZKP Wallet")
public class ZkpCreateWallet implements Callable<Void> {

    @Option(names = {"-m", "--zkp-wallet-manager"}, required = true, description = "ZKP wallet file path")
    public String zkpWalletManager;

    @Option(names = {"-p", "--zkp-wallet-manager-password"}, required = true, interactive = true, description = "ZKP wallet password")
    public char[] password;

    @Option(names = {"-r", "--file-remove"}, description = "if zkp wallet file exists, delete it and re-generate")
    public boolean existFileRemove = false;

    @Override
    public Void call() throws Exception {
        System.out.println("== new zkp wallet create call ==");
        CommandUtils.printedCommandMessage(this);

        // 0. remove file
        if (existFileRemove) {
            File file = new File(zkpWalletManager);
            if (file.exists()) {
                System.out.println("[SUCCESS] ZKP Wallet file remove : " + zkpWalletManager);
                file.delete();
            }
        }

        // 1. create zkp wallet
        IWKeyManager keyManager = new IWKeyManager(zkpWalletManager);
        Sha256 hash = Sha256.from(String.valueOf(password).getBytes());
        byte[] key = hash.getBytes();

        if(!keyManager.isUnLock()) {
            keyManager.unLock(key, new IWKeyManagerInterface.OnUnLockListener() {
                @Override
                public void onSuccess() {
                    try {
                        if(!keyManager.isPasswordSet()) {
                            keyManager.generateRandomKey("keyId", IWKey.ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue());
                        }
                        System.out.println("[SUCCESS] ZKP Wallet generate success...");
                    } catch(Exception e) {
                        System.out.println("[FAIL] ZKP Wallet generate fail...");
                    }
                }

                @Override
                public void onFail(int errCode) {
                    System.out.println("[FAIL] ZKP Wallet generate fail...");
                }

                @Override
                public void onCancel() {
                    System.out.println("[FAIL] Wallet generate fail...");
                }
            });
        }

        return null;
    }

}