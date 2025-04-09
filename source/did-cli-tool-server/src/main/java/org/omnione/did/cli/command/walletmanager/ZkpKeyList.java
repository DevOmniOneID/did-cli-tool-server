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

import java.util.List;
import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import org.omnione.did.cli.command.util.CommandUtils;
import org.omnione.did.wallet.key.WalletManagerFactory;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.wallet.key.data.KeyElement;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ZkpKeyList", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "Wallet ZKP Key list")
public class ZkpKeyList implements Callable<Void> {

    @Option(names = {"-m", "--wallet-manager"}, required = true, description = "Wallet file path")
    public String walletManager;

    @Option(names = {"-p", "--wallet-manager-password"}, required = true, interactive = true, description = "Wallet password")
    public char[] password;

    @Override
    public Void call() throws Exception {
        System.out.println("== get zkp key list call ==");
        CommandUtils.printedCommandMessage(this);

        // 1. connect
        WalletManagerInterface manager = WalletManagerFactory.getWalletManager(WalletManagerFactory.WalletManagerType.FILE);
        manager.connect(walletManager, password);

        // 2. get key
        if (manager.isConnect()) {
            //
        }
        return null;
    }

}