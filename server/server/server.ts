import * as express from 'express';
import {config as dotenvConfig} from 'dotenv';
import * as bodyParser from 'body-parser';
import registerHomeRoutes from './routes/home';

dotenvConfig();

const app = express();

app.set('port', process.env.PORT || 8000);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

registerHomeRoutes(app);

app.listen(app.get('port'), () => {
  console.log(('App is running at http://localhost:%d in %s mode'),
    app.get('port'), app.get('env'));
  console.log('Press CTRL-C to stop\n');
});

export default app;
