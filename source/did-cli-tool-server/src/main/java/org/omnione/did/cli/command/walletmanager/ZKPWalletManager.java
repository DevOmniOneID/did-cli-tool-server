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

import java.util.concurrent.Callable;
import org.omnione.did.cli.OmniCLI;
import picocli.CommandLine.Command;

@Command(name = "zkpWalletManager", mixinStandardHelpOptions = true, version = OmniCLI.VERSION, description = "ZKP WalletManager Command",
        subcommands = {
                ZkpCreateWallet.class,
                ZkpCredentialSchema.class,
                ZkpCredentialDefinition.class
        })
public class ZKPWalletManager implements Callable<Void> {

    @Override
    public Void call() throws Exception {
        return null;
    }

}

//java -jar did-cli-tool-server-1.0.0.jar zkpWalletManager createZkpWallet -m i.wallet -p
//java -jar did-cli-tool-server-1.0.0.jar zkpWalletManager createZkpCredSchema -s a.schema -sn mdl -sv 1.0 -st Tag1 -id "did:omn:NcYxiDXkpYi6ov5FcYDi1e" -al "zkpsex:zkpbirth"
//java -jar did-cli-tool-server-1.0.0.jar zkpWalletManager createZkpCredDef -s a.schema -m i.wallet -p